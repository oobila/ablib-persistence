package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.caches.Cache;
import com.github.oobila.bukkit.persistence.model.BackwardsCompatibility;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
abstract class BasePlayerPersistenceVehicle<K, V, C extends CacheItem<K, V>>
        implements PlayerPersistenceVehicle<K, V, C> {

    @Setter
    private Cache cache;
    private final List<BackwardsCompatibility> backwardsCompatibilityList = new ArrayList<>();
    
    @Override
    public void addBackwardsCompatibility(BackwardsCompatibility backwardsCompatibility) {
        backwardsCompatibilityList.add(backwardsCompatibility);
    }
}
