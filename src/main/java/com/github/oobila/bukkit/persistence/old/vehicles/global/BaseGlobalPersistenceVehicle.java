package com.github.oobila.bukkit.persistence.old.vehicles.global;

import com.github.oobila.bukkit.persistence.old.caches.Cache;
import com.github.oobila.bukkit.persistence.old.model.BackwardsCompatibility;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.vehicles.temp.PersistenceVehicle;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class BaseGlobalPersistenceVehicle<K, V, C extends CacheItem<K, V>> implements PersistenceVehicle<K, V, C> {

    private final Class<K> keyType;
    private Cache<K, V, C> cache;
    private final List<BackwardsCompatibility> backwardsCompatibilityList = new ArrayList<>();

    protected BaseGlobalPersistenceVehicle(Class<K> keyType) {
        this.keyType = keyType;
    }

    @SneakyThrows
    @Override
    public PersistenceVehicle<K, V, C> register(Cache<K, V, C> cache) {
        this.cache = cache;
        return this;
    }

    @Override
    public void addBackwardsCompatibility(BackwardsCompatibility backwardsCompatibility) {
        backwardsCompatibilityList.add(backwardsCompatibility);
    }

    public Plugin getPlugin() {
        return cache.getPlugin();
    }

}
