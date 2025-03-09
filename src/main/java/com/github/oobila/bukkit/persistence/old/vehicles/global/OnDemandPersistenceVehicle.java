package com.github.oobila.bukkit.persistence.old.vehicles.global;

import com.github.oobila.bukkit.persistence.old.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.old.vehicles.temp.PersistenceVehicle;

import java.util.Map;

public interface OnDemandPersistenceVehicle<K, V, C extends OnDemandCacheItem<K, V>>
        extends PersistenceVehicle<K, V, C> {

    Map<K, C> loadMetadata();

    C loadMetadataSingle(String name);

}
