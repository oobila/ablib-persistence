package com.github.oobila.bukkit.persistence.old.vehicles.temp;

import com.github.oobila.bukkit.persistence.old.model.OnDemandCacheItem;

public interface ClusterPersistenceVehicle<K, V, C extends OnDemandCacheItem<K, V>> extends PersistenceVehicle<K, V, C> {

    void saveSingle(C cacheItem);

}
