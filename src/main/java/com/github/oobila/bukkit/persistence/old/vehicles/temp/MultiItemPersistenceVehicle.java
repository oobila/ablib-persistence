package com.github.oobila.bukkit.persistence.old.vehicles.temp;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;

import java.util.Map;

public interface MultiItemPersistenceVehicle<J, K, V, C extends CacheItem<K, V>> extends PersistenceVehicle<K, V, C> {

    Map<K, C> load(J key);

    void save(Map<K, C> cacheItems);

    void delete(J key);

}
