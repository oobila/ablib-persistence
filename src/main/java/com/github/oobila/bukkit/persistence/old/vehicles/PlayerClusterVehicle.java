package com.github.oobila.bukkit.persistence.old.vehicles;

import com.github.oobila.bukkit.persistence.old.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import com.github.oobila.bukkit.persistence.old.vehicles.player.BasePlayerPersistenceVehicle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.oobila.bukkit.persistence.old.vehicles.utils.DirectoryUtils.append;
import static com.github.oobila.bukkit.persistence.old.vehicles.utils.DirectoryUtils.playerDir;
import static com.github.oobila.bukkit.persistence.old.vehicles.utils.BackwardsCompatibilityUtil.compatibility;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Getter
public class PlayerClusterVehicle<K, V, C extends CacheItem<K, V>> extends BasePlayerPersistenceVehicle<K, V, C> {

    private final Class<K> keyType;
    private final StorageAdapter storageAdapter;
    private final CodeAdapter<V> codeAdapter;

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, C> loadPlayer(Plugin plugin, String directory, UUID playerId) {
        codeAdapter.setPlugin(plugin);
        Map<K, C> map = new HashMap<>();
        List<String> items = storageAdapter.poll(
                plugin,
                playerDir(getPlayerDirectory(), playerId, directory)
        );
        for (String item : items) {
            List<StoredData> storedDataList = storageAdapter.read(
                    plugin,
                    playerDir(getPlayerDirectory(), playerId, append(directory, item))
            );
            storedDataList.forEach(storedData -> {
                K key = Serialization.deserialize(getKeyType(), storedData.getName());
                V value = codeAdapter.toObject(compatibility(this, storedData));
                C cacheItem = (C) new CacheItem<>(
                        getCodeAdapter().getType(),
                        key,
                        value,
                        storedData.getSize(),
                        storedData.getUpdatedDate(),
                        playerId
                );
                map.put(key, cacheItem);
            });
        }
        return map;
    }

    @Override
    public void save(Plugin plugin, String directory, Map<UUID, Map<K, C>> map) {
        map.forEach((playerId, innerMap) ->
                savePlayer(plugin, directory, playerId, innerMap)
        );
    }

    @Override
    public void savePlayer(Plugin plugin, String directory, UUID playerId, Map<K, C> map) {
        map.forEach((key, value) ->
                saveSingle(plugin, directory, playerId, value)
        );
    }

    @Override
    public void saveSingle(Plugin plugin, String directory, UUID playerId, C cacheItem) {
        String name = Serialization.serialize(cacheItem.getKey());
        String data = codeAdapter.fromObject(cacheItem.getData());
        StoredData storedData = new StoredData(name, data, 0, null);
        storageAdapter.write(
                plugin,
                playerDir(getPlayerDirectory(), playerId, append(directory, name)),
                List.of(storedData)
        );
    }
}