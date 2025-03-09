package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.plugin.Plugin;

public interface ClusterPersistenceVehicle<K, V, C extends CacheItem<K, V>> extends PersistenceVehicle<K, V, C> {

    C loadSingle(Plugin plugin, String directory, String name);
    void saveSingle(Plugin plugin, String directory, C cacheItem);
    void deleteSingle(Plugin plugin, String directory, K key);

}
