package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;
import org.bukkit.plugin.Plugin;

public interface OnDemandPersistenceVehicle<K,V> extends ClusterPersistenceVehicle<K,V> {

    OnDemandCacheItem<K,V> loadMetadataSingle(Plugin plugin, String directory, String name);

}
