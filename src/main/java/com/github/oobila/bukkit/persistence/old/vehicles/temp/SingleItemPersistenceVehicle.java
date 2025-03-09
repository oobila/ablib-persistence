package com.github.oobila.bukkit.persistence.old.vehicles.temp;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;

public interface SingleItemPersistenceVehicle<K, V, C extends CacheItem<K, V>> extends PersistenceVehicle<K, V, C> {

    C load(K key);

    void save(C cacheItem);

    void delete(K key);

}
