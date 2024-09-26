package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.adapters.vehicle.OnDemandPersistenceVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.OnDemandCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@SuppressWarnings("unused")
@Getter
public class AsyncOnDemandCache<K, V> implements AsyncWriteCache<K, V> {

    private final OnDemandCache<K,V> clusterCache;

    public AsyncOnDemandCache(String name, OnDemandPersistenceVehicle<K, V> vehicle) {
        this.clusterCache = new OnDemandCache<>(name, vehicle);
        clusterCache.getVehicle().setCache(this);
    }

    @Override
    public String getName() {
        return clusterCache.getName();
    }

    @Override
    public Plugin getPlugin() {
        return clusterCache.getPlugin();
    }

    @Override
    public PersistenceVehicle<K, V> getWriteVehicle() {
        return clusterCache.getWriteVehicle();
    }

    @Override
    public List<PersistenceVehicle<K, V>> getReadVehicles() {
        return clusterCache.getReadVehicles();
    }

    @Override
    public void load(Plugin plugin) {
        clusterCache.load(plugin);
    }

    @Override
    public void unload() {
        clusterCache.unload();
    }

    @Override
    public void save() {
        //do nothing
    }

    @Override
    public void getValue(K key, Consumer<V> consumer) {
        runTaskAsync(() -> consumer.accept(clusterCache.getValue(key)));
    }

    @Override
    public void get(K key, Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> consumer.accept(clusterCache.get(key)));
    }

    @Override
    public void putValue(K key, V value, Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> consumer.accept(clusterCache.putValue(key, value)));
    }

    @Override
    public void remove(K key, Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> consumer.accept(clusterCache.remove(key)));
    }

    @Override
    public void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<CacheItem<K, V>>> consumer) {
        runTaskAsync(() -> consumer.accept(clusterCache.removeBefore(zonedDateTime)));
    }
}
