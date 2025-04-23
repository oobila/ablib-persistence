package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.observers.ReadCacheOperationObserver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;


@Getter
@SuppressWarnings("unused")
public class AsyncReadOnlyCache<K, V> implements AsyncReadCache<K, V, CacheItem<K, V>> {

    @Setter(AccessLevel.PROTECTED)
    private Plugin plugin;
    private final PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle;
    private final List<PersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles;

    protected final List<ReadCacheOperationObserver<K, V>> rOperationObservers = new ArrayList<>();
    protected final Map<K, CacheItem<K, V>> nullCache = new HashMap<>();
    protected final Map<UUID, Map<K, CacheItem<K, V>>> localCache = new HashMap<>();

    public AsyncReadOnlyCache(PersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        this(vehicle, vehicle);
    }

    public AsyncReadOnlyCache(PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle, PersistenceVehicle<K, V, CacheItem<K, V>> readVehicle) {
        this(writeVehicle, List.of(readVehicle));
    }

    public AsyncReadOnlyCache(PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle, List<PersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles) {
        this.writeVehicle = writeVehicle;
        this.readVehicles = readVehicles;
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
        writeVehicle.setPlugin(plugin);
        nullCache.clear();
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
    public boolean isLoaded(UUID partition) {
        return localCache.containsKey(partition);
    }

    @Override
    public void unload() {
        nullCache.clear();
        localCache.clear();
        localCache.putIfAbsent(null, nullCache);
        rOperationObservers.forEach(ReadCacheOperationObserver::onUnload);
    }

    @Override
    public void unload(UUID partition) {
        localCache.remove(partition);
        rOperationObservers.forEach(observer -> observer.onUnload(partition));
    }

    @Override
    public void getValue(K key, @NotNull Consumer<V> consumer) {
        getValue(null, key, consumer);
    }

    @Override
    public void getValue(UUID partition, K key, @NotNull Consumer<V> consumer) {
        runTaskAsync(() -> {
            V value = localCache.get(partition).get(key).getData();
            consumer.accept(value);
        });
    }

    @Override
    public CacheItem<K, V> get(UUID partition, K key) {
        if (localCache.containsKey(partition)) {
            return localCache.get(partition).get(key);
        } else {
            return null;
        }
    }

    @Override
    public Collection<K> keySet() {
        return nullCache.keySet();
    }

    @Override
    public Collection<K> keySet(UUID partition) {
        if (localCache.containsKey(partition)) {
            return localCache.get(partition).keySet();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<CacheItem<K, V>> values() {
        return nullCache.values();
    }

    @Override
    public Collection<CacheItem<K, V>> values(UUID partition) {
        if (localCache.containsKey(partition)) {
            return localCache.get(partition).values();
        } else {
            return Collections.emptyList();
        }
    }

    public void addObserver(ReadCacheOperationObserver<K, V> observer) {
        rOperationObservers.add(observer);
    }
}
