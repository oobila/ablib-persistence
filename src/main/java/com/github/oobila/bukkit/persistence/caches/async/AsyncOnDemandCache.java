package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

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
public class AsyncOnDemandCache<K, V> implements AsyncWriteCache<K, V> {

    @Setter(AccessLevel.PROTECTED)
    private Plugin plugin;
    private final PersistenceVehicle<K, V> writeVehicle;
    private final List<PersistenceVehicle<K, V>> readVehicles;

    private final List<K> nullKeys = new ArrayList<>();
    private final Map<UUID, List<K>> keys = new HashMap<>();

    public AsyncOnDemandCache(PersistenceVehicle<K, V> vehicle) {
        this(vehicle, vehicle);
    }

    public AsyncOnDemandCache(PersistenceVehicle<K, V> writeVehicle, PersistenceVehicle<K, V> readVehicle) {
        this(writeVehicle, List.of(readVehicle));
    }

    public AsyncOnDemandCache(PersistenceVehicle<K, V> writeVehicle, List<PersistenceVehicle<K, V>> readVehicles) {
        this.writeVehicle = writeVehicle;
        this.readVehicles = readVehicles;
        keys.put(null, nullKeys);
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load(UUID partition) {
        //not async as this needs to be accessible asap
        List<K> partitionKeys = new ArrayList<>();
        readVehicles.forEach(vehicle -> partitionKeys.addAll(vehicle.keys(partition)));
        keys.put(partition, partitionKeys);
    }

    @Override
    public void unload() {
        //do nothing
    }

    @Override
    public void unload(UUID partition) {
        keys.remove(partition);
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
    public void getValue(K key, Consumer<V> consumer) {
        getValue(null, key, consumer);
    }

    @Override
    public void getValue(UUID partition, K key, Consumer<V> consumer) {
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
    public Collection<K> keys() {
        throw new PersistenceRuntimeException("operation not supported");
    }

    @Override
    public Collection<K> keys(UUID partition) {
        throw new PersistenceRuntimeException("operation not supported");
    }

    @Override
    public void putValue(K key, V value, Consumer<OnDemandCacheItem<K, V>> consumer) {
        putValue(null, key, value, consumer);
    }

    @Override
    public void putValue(UUID partition, K key, V value, Consumer<OnDemandCacheItem<K, V>> consumer) {
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
        });
    }

    @Override
    public void remove(K key, Consumer<OnDemandCacheItem<K, V>> consumer) {
        remove(null, key, consumer);
    }

    @Override
    public void remove(UUID partition, K key, Consumer<OnDemandCacheItem<K, V>> consumer) {
        runTaskAsync(() -> writeVehicle.delete(plugin, partition, key));
    }

    @Override
    public void clear(UUID partition, Consumer<List<OnDemandCacheItem<K, V>>> consumer) {
        //do nothing
    }

    @Override
    public void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<OnDemandCacheItem<K, V>>> consumer) {
        throw new PersistenceRuntimeException("operation not supported, please do this manually");
    }
}
