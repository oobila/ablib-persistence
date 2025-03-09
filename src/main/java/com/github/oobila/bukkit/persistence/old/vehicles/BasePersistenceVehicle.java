package com.github.oobila.bukkit.persistence.old.vehicles;

import com.github.oobila.bukkit.persistence.old.caches.Cache;
import com.github.oobila.bukkit.persistence.old.model.BackwardsCompatibility;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.vehicles.global.GlobalPersistenceVehicle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
abstract class BasePersistenceVehicle<K, V, C extends CacheItem<K, V>> implements GlobalPersistenceVehicle<K, V, C> {

    @Setter
    private Cache<K, V, C> cache;
    private final List<BackwardsCompatibility> backwardsCompatibilityList = new ArrayList<>();

    @Override
    public void addBackwardsCompatibility(BackwardsCompatibility backwardsCompatibility) {
        backwardsCompatibilityList.add(backwardsCompatibility);
    }
}
