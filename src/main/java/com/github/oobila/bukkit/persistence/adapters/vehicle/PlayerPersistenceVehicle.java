package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.model.BackwardsCompatibility;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public interface PlayerPersistenceVehicle<K, V> {

    Map<K, CacheItem<K,V>> loadPlayer(Plugin plugin, String directory, UUID playerId);

    void save(Plugin plugin, String directory, Map<UUID, Map<K, CacheItem<K,V>>> map);
    void savePlayer(Plugin plugin, String directory, UUID playerId, Map<K, CacheItem<K,V>> map);
    void saveSingle(Plugin plugin, String directory, UUID playerId, CacheItem<K,V> cacheItem);
    StorageAdapter getStorageAdapter();
    Class<K> getKeyType();
    default String getPlayerDirectory() {
        return "playerData/";
    }
    void addBackwardsCompatibility(BackwardsCompatibility backwardsCompatibility);
    List<BackwardsCompatibility> getBackwardsCompatibilityList();
}
