package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.model.BackwardsCompatibility;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
abstract class BasePersistenceVehicle<K, V> implements PersistenceVehicle<K, V> {

    private final List<BackwardsCompatibility> backwardsCompatibilityList = new ArrayList<>();

    @Override
    public void addBackwardsCompatibility(BackwardsCompatibility backwardsCompatibility) {
        backwardsCompatibilityList.add(backwardsCompatibility);
    }
}
