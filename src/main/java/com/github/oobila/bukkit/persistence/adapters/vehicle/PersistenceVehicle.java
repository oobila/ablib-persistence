package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.caches.Cache;
import com.github.oobila.bukkit.persistence.model.BackwardsCompatibility;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface PersistenceVehicle<K, V> {

    Cache getCache();
    void setCache(Cache cache);
    Map<K, CacheItem<K,V>> load(Plugin plugin, String directory);
    void save(Plugin plugin, String directory, Map<K, CacheItem<K,V>> map);
    void saveSingle(Plugin plugin, String directory, CacheItem<K,V> cacheItem);
    StorageAdapter getStorageAdapter();
    Class<K> getKeyType();
    void addBackwardsCompatibility(BackwardsCompatibility backwardsCompatibility);
    List<BackwardsCompatibility> getBackwardsCompatibilityList();

}
