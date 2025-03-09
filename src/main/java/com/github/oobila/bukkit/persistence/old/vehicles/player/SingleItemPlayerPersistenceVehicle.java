package com.github.oobila.bukkit.persistence.old.vehicles.player;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.vehicles.temp.PersistenceVehicle;

public interface SingleItemPlayerPersistenceVehicle<K, V, C extends CacheItem<K, V>> extends PersistenceVehicle<K, V, C> {

    C load(K key);

    void save(C cacheItem);

    void delete(K key);

}
