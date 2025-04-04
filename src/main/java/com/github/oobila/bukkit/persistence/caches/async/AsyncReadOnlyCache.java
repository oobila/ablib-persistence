package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;


@Getter
public class AsyncReadOnlyCache<K, V> implements AsyncReadCache<K, V> {

    @Setter(AccessLevel.PROTECTED)
    private Plugin plugin;
    private final PersistenceVehicle<K, V> writeVehicle;
    private final List<PersistenceVehicle<K, V>> readVehicles;

    protected final Map<K, OnDemandCacheItem<K, V>> nullCache = new HashMap<>();
    protected final Map<UUID, Map<K, OnDemandCacheItem<K, V>>> localCache = new HashMap<>();

    public AsyncReadOnlyCache(PersistenceVehicle<K, V> vehicle) {
        this(vehicle, vehicle);
    }

    public AsyncReadOnlyCache(PersistenceVehicle<K, V> writeVehicle, PersistenceVehicle<K, V> readVehicle) {
        this(writeVehicle, List.of(readVehicle));
    }

    public AsyncReadOnlyCache(PersistenceVehicle<K, V> writeVehicle, List<PersistenceVehicle<K, V>> readVehicles) {
        this.writeVehicle = writeVehicle;
        this.readVehicles = readVehicles;
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
        nullCache.clear();
        writeVehicle.copyDefaults();
        Map<K, CacheItem<K, V>> uncastMap = new HashMap<>();
        readVehicles.forEach(vehicle -> uncastMap.putAll(vehicle.load(plugin)));
        uncastMap.forEach((k, cacheItem) -> nullCache.put(k, (OnDemandCacheItem<K, V>) cacheItem));
    }

    @Override
    public void load(UUID partition) {
        localCache.putIfAbsent(partition, new HashMap<>());
        Map<K, OnDemandCacheItem<K, V>> map = localCache.get(partition);
        Map<K, CacheItem<K, V>> uncastMap = new HashMap<>();
        readVehicles.forEach(vehicle -> uncastMap.putAll(vehicle.load(plugin, partition)));
        uncastMap.forEach((k, cacheItem) -> map.put(k, (OnDemandCacheItem<K, V>) cacheItem));
    }

    @Override
    public void unload() {
        nullCache.clear();
        localCache.clear();
        localCache.putIfAbsent(null, nullCache);
    }

    @Override
    public void unload(UUID partition) {
        localCache.remove(partition);
    }

    @Override
    public void getValue(K key, Consumer<V> consumer) {
        runTaskAsync(() -> {
            V value = nullCache.get(key).getData();
            consumer.accept(value);
        });
    }

    @Override
    public void getValue(UUID partition, K key, Consumer<V> consumer) {
        runTaskAsync(() -> {
            V value = localCache.get(partition).get(key).getData();
            consumer.accept(value);
        });
    }

    @Override
    public OnDemandCacheItem<K, V> get(UUID partition, K key) {
        return localCache.get(partition).get(key);
    }

    @Override
    public Collection<K> keys() {
        return nullCache.keySet();
    }

    @Override
    public Collection<K> keys(UUID partition) {
        return localCache.get(partition).keySet();
    }

    @Override
    public Collection<OnDemandCacheItem<K, V>> values() {
        return nullCache.values();
    }

    @Override
    public Collection<OnDemandCacheItem<K, V>> values(UUID partition) {
        return localCache.get(partition).values();
    }
}
