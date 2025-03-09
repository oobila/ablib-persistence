package com.github.oobila.bukkit.persistence.old.vehicles.player;

import com.github.oobila.bukkit.persistence.old.model.OnDemandCacheItem;

import java.util.UUID;

public interface OnDemandPersistenceVehicle<K, V, C extends OnDemandCacheItem<K, V>> extends PlayerPersistenceVehicle<K, V, C> {

    C loadMetadataSingle(UUID playerId, String name);

    void deleteSingle(UUID playerId, K key);
}
