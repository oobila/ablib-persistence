package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.observers.ReadCacheOperationObserver;
import com.github.oobila.bukkit.persistence.observers.WriteCacheOperationObserver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@Getter
public class AsyncOnDemandCache<K, V> implements AsyncWriteCache<K, V, OnDemandCacheItem<K, V>> {

    @Setter(AccessLevel.PROTECTED)
    private Plugin plugin;
    private final PersistenceVehicle<K, V, OnDemandCacheItem<K, V>> writeVehicle;
    private final List<PersistenceVehicle<K, V, OnDemandCacheItem<K, V>>> readVehicles;

    protected final List<WriteCacheOperationObserver<K, V>> wOperationObservers = new ArrayList<>();
    protected final List<ReadCacheOperationObserver<K, V>> rOperationObservers = new ArrayList<>();
    private final List<K> nullKeys = new ArrayList<>();
    private final Map<UUID, List<K>> keys = new HashMap<>();

    public AsyncOnDemandCache(PersistenceVehicle<K, V, OnDemandCacheItem<K, V>> vehicle) {
        this(vehicle, vehicle);
    }

    public AsyncOnDemandCache(PersistenceVehicle<K, V, OnDemandCacheItem<K, V>> writeVehicle,
                              PersistenceVehicle<K, V, OnDemandCacheItem<K, V>> readVehicle) {
        this(writeVehicle, List.of(readVehicle));
    }

    public AsyncOnDemandCache(PersistenceVehicle<K, V, OnDemandCacheItem<K, V>> writeVehicle,
                              List<PersistenceVehicle<K, V, OnDemandCacheItem<K, V>>> readVehicles) {
        this.writeVehicle = writeVehicle;
        this.readVehicles = readVehicles;
        keys.put(null, nullKeys);
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
        List<K> local = new ArrayList<>();
        readVehicles.forEach(vehicle -> local.addAll(vehicle.keys()));
        nullKeys.addAll(local);
        local.forEach(k ->
                rOperationObservers.forEach(observer -> observer.onLoad(k, null))
        );
    }

    @Override
    public void load(UUID partition) {
        //not async as this needs to be accessible asap
        List<K> partitionKeys = new ArrayList<>();
        readVehicles.forEach(vehicle -> partitionKeys.addAll(vehicle.keys(partition)));
        keys.put(partition, partitionKeys);
        partitionKeys.forEach(k ->
                rOperationObservers.forEach(observer -> observer.onLoad(partition, k, null))
        );
    }

    @Override
    public void unload() {
        nullKeys.clear();
        rOperationObservers.forEach(ReadCacheOperationObserver::onUnload);
    }

    @Override
    public void unload(UUID partition) {
        keys.remove(partition);
        rOperationObservers.forEach(observer -> observer.onUnload(partition));
    }

    @Override
    public void save() {
        //do nothing
    }

    @Override
    public void save(UUID partition) {
        //do nothing
    }

    @Override
    public void getValue(K key, @NotNull Consumer<V> consumer) {
        getValue(null, key, consumer);
    }

    @Override
    public void getValue(UUID partition, K key, @NotNull Consumer<V> consumer) {
        runTaskAsync(() -> {
            List<CacheItem<K, V>> cacheItems = new ArrayList<>();
            readVehicles.forEach(vehicle -> cacheItems.add(vehicle.load(plugin, partition, key)));
            if (cacheItems.size() == 1) {
                consumer.accept(cacheItems.iterator().next().getData());
            } else if (cacheItems.isEmpty()) {
                consumer.accept(null);
            } else {
                throw new PersistenceRuntimeException("more than one item retrieved for a given key");
            }
        });
    }

    public void get(UUID partition, K key, Consumer<OnDemandCacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            List<CacheItem<K, V>> cacheItems = new ArrayList<>();
            readVehicles.forEach(vehicle -> cacheItems.add(vehicle.load(plugin, partition, key)));
            if (cacheItems.size() == 1) {
                consumer.accept((OnDemandCacheItem<K, V>) cacheItems.iterator().next());
            } else if (cacheItems.isEmpty()) {
                consumer.accept(null);
            } else {
                throw new PersistenceRuntimeException("more than one item retrieved for a given key");
            }
        });
    }

    @Override
    public OnDemandCacheItem<K, V> get(UUID partition, K key) {
        throw new PersistenceRuntimeException("operation not supported, please use method with consumer");
    }

    @Override
    public Collection<OnDemandCacheItem<K, V>> values() {
        throw new PersistenceRuntimeException("operation not supported");
    }

    @Override
    public Collection<OnDemandCacheItem<K, V>> values(UUID partition) {
        throw new PersistenceRuntimeException("operation not supported");
    }

    @Override
    public Collection<K> keySet() {
        throw new PersistenceRuntimeException("operation not supported");
    }

    @Override
    public Collection<K> keySet(UUID partition) {
        throw new PersistenceRuntimeException("operation not supported");
    }

    @Override
    public void putValue(K key, V value, @NotNull Consumer<OnDemandCacheItem<K, V>> consumer) {
        putValue(null, key, value, consumer);
    }

    @Override
    public void putValue(UUID partition, K key, V value, @NotNull Consumer<OnDemandCacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            OnDemandCacheItem<K, V> cacheItem = new OnDemandCacheItem<>(
                    writeVehicle.getCodeAdapter().getType(),
                    partition,
                    key,
                    value,
                    0,
                    null,
                    null
            );
            writeVehicle.save(plugin, partition, key, cacheItem);
            if (partition == null) {
                wOperationObservers.forEach(observer -> observer.onPut(key, value));
            } else {
                wOperationObservers.forEach(observer -> observer.onPut(partition, key, value));
            }
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void remove(K key, @NotNull Consumer<OnDemandCacheItem<K, V>> consumer) {
        remove(null, key, consumer);
    }

    @Override
    public void remove(UUID partition, K key, @NotNull Consumer<OnDemandCacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            writeVehicle.delete(plugin, partition, key);
            if (partition == null) {
                wOperationObservers.forEach(observer -> observer.onRemove(key, null));
            } else {
                wOperationObservers.forEach(observer -> observer.onRemove(partition, key, null));
            }
            consumer.accept(null);
        });
    }

    @Override
    public void clear(UUID partition, @NotNull Consumer<List<OnDemandCacheItem<K, V>>> consumer) {
        //do nothing
    }

    @Override
    public void removeBefore(ZonedDateTime zonedDateTime, @NotNull Consumer<List<OnDemandCacheItem<K, V>>> consumer) {
        throw new PersistenceRuntimeException("operation not supported, please do this manually");
    }

    public void addObserver(ReadCacheOperationObserver<K, V> observer) {
        if (observer instanceof WriteCacheOperationObserver<K,V> writeCacheOperationObserver) {
            wOperationObservers.add(writeCacheOperationObserver);
        } else {
            rOperationObservers.add(observer);
        }
    }
}
