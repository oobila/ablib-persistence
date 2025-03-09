package com.github.oobila.bukkit.persistence.old.vehicles.global;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.vehicles.temp.PersistenceVehicle;

import java.util.Map;


public interface GlobalPersistenceVehicle<K, V, C extends CacheItem<K, V>> extends PersistenceVehicle<K, V, C> {

    Map<K, C> load();

    void save(Map<K, C> map);
}
