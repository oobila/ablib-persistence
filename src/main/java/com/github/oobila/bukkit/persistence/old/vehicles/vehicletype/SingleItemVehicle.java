package com.github.oobila.bukkit.persistence.old.vehicles.vehicletype;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;

public interface SingleItemVehicle<K, V, C extends CacheItem<K, V>> {

    V load();
    void store(V value);

}
