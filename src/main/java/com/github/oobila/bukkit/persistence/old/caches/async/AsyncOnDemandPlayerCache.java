package com.github.oobila.bukkit.persistence.old.caches.async;

import com.github.oobila.bukkit.persistence.old.vehicles.global.OnDemandPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.vehicles.global.GlobalPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.caches.standard.OnDemandCache;
import com.github.oobila.bukkit.persistence.old.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.old.observers.PlayerObserver;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@SuppressWarnings("unused")
@Getter
public class AsyncOnDemandPlayerCache<K, V> implements AsyncPlayerWriteCache<K, V, OnDemandCacheItem<K, V>> {

    private final OnDemandCache<K,V> clusterCache;

    public AsyncOnDemandPlayerCache(String name, OnDemandPersistenceVehicle<K, V, OnDemandCacheItem<K, V>> vehicle) {
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
    public GlobalPersistenceVehicle<K, V, OnDemandCacheItem<K, V>> getWriteVehicle() {
        return clusterCache.getWriteVehicle();
    }

    @Override
    public List<GlobalPersistenceVehicle<K, V, OnDemandCacheItem<K, V>>> getReadVehicles() {
        return clusterCache.getReadVehicles();
    }

    @Override
    public void load(Plugin plugin) {
        clusterCache.load(plugin);
    }

    @Override
    public void unloadPlayer(UUID id) {

    }

    @Override
    public void addPlayerObserver(PlayerObserver<K, V, OnDemandCacheItem<K, V>> playerObserver) {

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

    public OnDemandCacheItem<K, V> get(K key) {
        return clusterCache.get(key);
    }

    @Override
    public Collection<OnDemandCacheItem<K, V>> values() {
        return clusterCache.values();
    }

    @Override
    public void putValue(K key, V value, Consumer<OnDemandCacheItem<K, V>> consumer) {
        runTaskAsync(() -> consumer.accept(clusterCache.putValue(key, value)));
    }

    @Override
    public void remove(K key, Consumer<OnDemandCacheItem<K, V>> consumer) {
        runTaskAsync(() -> consumer.accept(clusterCache.remove(key)));
    }

    @Override
    public void putValue(UUID id, K key, V value, Consumer<OnDemandCacheItem<K, V>> consumer) {

    }

    @Override
    public void remove(UUID id, K key, Consumer<OnDemandCacheItem<K, V>> consumer) {

    }

    @Override
    public void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<OnDemandCacheItem<K, V>>> consumer) {
        runTaskAsync(() -> consumer.accept(clusterCache.removeBefore(zonedDateTime)));
    }

    @Override
    public void savePlayer(UUID id, Consumer<Map<K, OnDemandCacheItem<K, V>>> consumer) {

    }

    @Override
    public void getValue(UUID id, K key, Consumer<V> consumer) {

    }

    @Override
    public OnDemandCacheItem<K, V> get(UUID id, K key) {
        return null;
    }

    @Override
    public Map<K, OnDemandCacheItem<K, V>> values(UUID id) {
        return null;
    }

    @Override
    public void loadPlayer(UUID id, Consumer<Map<K, OnDemandCacheItem<K, V>>> consumer) {

    }
}
