package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.caches.Cache;
import com.github.oobila.bukkit.persistence.model.BackwardsCompatibility;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
abstract class BasePersistenceVehicle<K, V, C extends CacheItem<K, V>> implements PersistenceVehicle<K, V, C> {

    @Setter
    protected Cache cache;
    private final List<BackwardsCompatibility> backwardsCompatibilityList = new ArrayList<>();

    @Override
    public void addBackwardsCompatibility(BackwardsCompatibility backwardsCompatibility) {
        backwardsCompatibilityList.add(backwardsCompatibility);
    }
}
