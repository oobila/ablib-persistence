package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.observers.CacheLoadObserver;
import com.github.oobila.bukkit.persistence.observers.CacheObserver;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.observers.ReadCacheOperationObserver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
public class ReadOnlyCache<K, V> implements StandardReadCache<K, V> {

    @Setter(AccessLevel.PROTECTED)
    private Plugin plugin;
    private final List<PersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles;
    private final PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle;

    protected final List<CacheLoadObserver> cacheObservers = new ArrayList<>();
    protected final List<ReadCacheOperationObserver<K, V>> rOperationObservers = new ArrayList<>();
    protected final Map<K, CacheItem<K, V>> nullCache = new HashMap<>();
    protected final Map<UUID, Map<K, CacheItem<K, V>>> localCache = new HashMap<>();


    public ReadOnlyCache(PersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        this(vehicle, vehicle);
    }

    public ReadOnlyCache(PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle, PersistenceVehicle<K, V, CacheItem<K, V>> readVehicle) {
        this(writeVehicle, List.of(readVehicle));
    }

    public ReadOnlyCache(PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle, List<PersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles) {
        this.readVehicles = readVehicles;
        readVehicles.forEach(readVehicle -> readVehicle.setCache(this));
        this.writeVehicle = writeVehicle;
        writeVehicle.setCache(this);
        localCache.putIfAbsent(null, nullCache);
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
        writeVehicle.setPlugin(plugin);
        unload();
        writeVehicle.copyDefaults();
        readVehicles.forEach(vehicle -> {
            Map<K, CacheItem<K, V>> loadedItems = vehicle.load(plugin);
            rOperationObservers.forEach(observer ->
                loadedItems.forEach((k, cacheItem) ->
                    observer.onLoad(k, cacheItem.getData())
                )
            );
            nullCache.putAll(loadedItems);
        });
        cacheObservers.forEach(CacheLoadObserver::onCacheLoad);
    }

    @Override
    public void load(UUID partition) {
        localCache.putIfAbsent(partition, new HashMap<>());
        Map<K, CacheItem<K, V>> map = localCache.get(partition);
        readVehicles.forEach(vehicle -> {
            Map<K, CacheItem<K, V>> loadedItems = vehicle.load(plugin, partition);
            rOperationObservers.forEach(observer ->
                loadedItems.forEach((k, cacheItem) ->
                    observer.onLoad(partition, k, cacheItem.getData())
                )
            );
            map.putAll(loadedItems);
        });
    }

    @Override
    public void unload() {
        nullCache.clear();
        localCache.clear();
        localCache.putIfAbsent(null, nullCache);
        rOperationObservers.forEach(ReadCacheOperationObserver::onUnload);
        cacheObservers.stream().filter(CacheObserver.class::isInstance)
                .map(CacheObserver.class::cast).forEach(CacheObserver::onCacheUnload);
    }

    @Override
    public void unload(UUID partition) {
        localCache.remove(partition);
        rOperationObservers.forEach(observer -> observer.onUnload(partition));
    }

    @Override
    @SneakyThrows
    public V getValue(K key) {
        return get(key).getData();
    }

    @Override
    @SneakyThrows
    public CacheItem<K, V> get(UUID partition, K key) {
        return localCache.get(partition).get(key);
    }

    @Override
    @SneakyThrows
    public V getValue(UUID partition, K key) {
        return get(partition, key).getData();
    }

    public int size() {
        return Math.max(nullCache.size(), localCache.size() - 1);
    }

    public boolean isEmpty() {
        return nullCache.isEmpty() && localCache.size() < 2;
    }

    @SuppressWarnings({"unchecked", "java:S1905"})
    public boolean containsKey(Object key) {
        return nullCache.containsKey((K) key);
    }

    @SuppressWarnings({"all"})
    public boolean containsValue(Object value) {
        return nullCache.containsValue(value);
    }

    @SuppressWarnings({"unchecked", "java:S1905"})
    public CacheItem<K, V> get(Object key) {
        return nullCache.get((K) key);
    }

    @NotNull
    public Set<K> keySet() {
        return nullCache.keySet();
    }

    @NotNull
    public Collection<CacheItem<K, V>> values() {
        return nullCache.values();
    }

    @Override
    public Collection<K> keySet(UUID partition) {
        return localCache.get(partition).keySet();
    }

    @Override
    public Collection<CacheItem<K, V>> values(UUID partition) {
        return localCache.get(partition).values();
    }

    @NotNull
    public Set<Map.Entry<K, CacheItem<K, V>>> entrySet() {
        return nullCache.entrySet();
    }

    public void addObserver(CacheLoadObserver observer) {
        cacheObservers.add(observer);
    }

    public void addObserver(ReadCacheOperationObserver<K, V> observer) {
        rOperationObservers.add(observer);
    }

}
