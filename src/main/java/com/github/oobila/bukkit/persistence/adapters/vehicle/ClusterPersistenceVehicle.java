package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.plugin.Plugin;

public interface ClusterPersistenceVehicle<K, V> extends PersistenceVehicle<K, V> {

    CacheItem<K,V> loadSingle(Plugin plugin, String directory, String name);
    void saveSingle(Plugin plugin, String directory, CacheItem<K,V> cacheItem);
    void deleteSingle(Plugin plugin, String directory, K key);

}
