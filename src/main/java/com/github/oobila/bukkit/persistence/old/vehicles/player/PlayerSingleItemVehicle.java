package com.github.oobila.bukkit.persistence.old.vehicles.player;

import com.github.oobila.bukkit.persistence.old.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.oobila.bukkit.persistence.old.vehicles.utils.BackwardsCompatibilityUtil.compatibility;
import static com.github.oobila.bukkit.persistence.old.vehicles.utils.DirectoryUtils.playerDir;

@SuppressWarnings("unused")
@Getter
public class PlayerSingleItemVehicle<K, V, C extends CacheItem<K, V>> extends BasePlayerPersistenceVehicle<K, V, C> {

    private final StorageAdapter storageAdapter;
    private final CodeAdapter<V> codeAdapter;

    public PlayerSingleItemVehicle(Class<K> keyType, StorageAdapter storageAdapter, CodeAdapter<V> codeAdapter) {
        super(keyType);
        this.storageAdapter = storageAdapter;
        this.codeAdapter = codeAdapter;
    }

    @Override
    public void load(Plugin plugin) {
        codeAdapter.setPlugin(plugin);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, C> loadPlayer(Player player) {
        Map<K, C> map = new HashMap<>();
        List<StoredData> storedDataList = storageAdapter.read(
                getPlugin(),
                playerDir(getPlayerDirectory(), player.getUniqueId(), null)
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
                    player.getUniqueId()
            );
            map.put(key, cacheItem);
        });
        return map;
    }

    @Override
    public void save(Map<Player, Map<K, C>> map) {
        map.forEach(this::savePlayer);
    }

    @Override
    public void savePlayer(Player player, Map<K, C> map) {
        Map.Entry<K, C> entry = map.entrySet().iterator().next();
        saveSingle(player, entry.getValue());
    }

    @Override
    public void saveSingle(Player player, C cacheItem) {
        String name = Serialization.serialize(cacheItem.getKey());
        String data = codeAdapter.fromObject(cacheItem.getData());
        StoredData storedData = new StoredData(name, data, 0, null);
        storageAdapter.write(
                getPlugin(),
                playerDir(getPlayerDirectory(), player.getUniqueId(), getCache().getName()),
                List.of(storedData)
        );
    }
}
