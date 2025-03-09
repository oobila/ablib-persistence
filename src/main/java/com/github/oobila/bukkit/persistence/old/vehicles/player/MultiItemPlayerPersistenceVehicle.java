package com.github.oobila.bukkit.persistence.old.vehicles.player;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.vehicles.temp.PersistenceVehicle;

import java.util.Map;

public interface MultiItemPlayerPersistenceVehicle<J, K, V, C extends CacheItem<K, V>> extends PersistenceVehicle<K, V, C> {

    Map<K, C> load(J key);

    void save(Map<K, C> cacheItems);

    void delete(J key);

}
