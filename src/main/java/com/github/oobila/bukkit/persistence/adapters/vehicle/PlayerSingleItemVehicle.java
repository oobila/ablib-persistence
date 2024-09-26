package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.oobila.bukkit.persistence.adapters.utils.DirectoryUtils.playerDir;
import static com.github.oobila.bukkit.persistence.utils.BackwardsCompatibilityUtil.compatibility;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Getter
public class PlayerSingleItemVehicle<K, V, C extends CacheItem<K, V>> extends BasePlayerPersistenceVehicle<K, V, C> {

    private final Class<K> keyType;
    private final StorageAdapter storageAdapter;
    private final CodeAdapter<V> codeAdapter;

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, C> loadPlayer(Plugin plugin, String directory, UUID playerId) {
        codeAdapter.setPlugin(plugin);
        Map<K, C> map = new HashMap<>();
        List<StoredData> storedDataList = storageAdapter.read(
                plugin,
                playerDir(getPlayerDirectory(), playerId, directory)
        );
        storedDataList.forEach(storedData -> {
            K key = Serialization.deserialize(getKeyType(), storedData.getName());
            V value = codeAdapter.toObject(compatibility(this, storedData));
            C cacheItem = (C) new CacheItem<>(key, value, storedData);
            map.put(key, cacheItem);
        });
        return map;
    }

    @Override
    public void save(Plugin plugin, String directory, Map<UUID, Map<K, C>> map) {
        map.forEach((playerId, innerMap) -> savePlayer(plugin, directory, playerId, innerMap));
    }

    @Override
    public void savePlayer(Plugin plugin, String directory, UUID playerId, Map<K, C> map) {
        Map.Entry<K, C> entry = map.entrySet().iterator().next();
        saveSingle(plugin, directory, playerId, entry.getValue());
    }

    @Override
    public void saveSingle(Plugin plugin, String directory, UUID playerId, C cacheItem) {
        String name = Serialization.serialize(cacheItem.getKey());
        String data = codeAdapter.fromObject(cacheItem.getData());
        StoredData storedData = new StoredData(name, data, 0, null);
        storageAdapter.write(
                plugin,
                playerDir(getPlayerDirectory(), playerId, directory),
                List.of(storedData)
        );
    }
}
