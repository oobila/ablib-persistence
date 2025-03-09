package com.github.oobila.bukkit.persistence.old.vehicles.vehicletype;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;

import java.util.Map;

public interface MultiItemVehicle<K, V, C extends CacheItem<K, V>> {

    Map<K, V> load();
    void store(Map<K, V> map);

}
