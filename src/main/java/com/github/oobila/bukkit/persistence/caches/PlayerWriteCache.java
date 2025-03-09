package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;

@SuppressWarnings("unused")
public interface PlayerWriteCache<K, V, C extends CacheItem<K, V>> extends Cache {

    void save();

    PlayerPersistenceVehicle<K, V, C> getWriteVehicle();

}
