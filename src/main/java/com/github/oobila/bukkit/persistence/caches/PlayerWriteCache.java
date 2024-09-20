package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;

@SuppressWarnings("unused")
public interface PlayerWriteCache<K, V> extends Cache {

    void save();

    PlayerPersistenceVehicle<K, V> getWriteVehicle();

}
