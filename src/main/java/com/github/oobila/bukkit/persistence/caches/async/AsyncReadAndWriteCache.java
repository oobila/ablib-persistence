package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.observers.WriteCacheOperationObserver;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;
import static com.github.oobila.bukkit.common.ABCommon.runTaskLater;

@SuppressWarnings({"unused"})
@Getter
public class AsyncReadAndWriteCache<K, V> extends AsyncReadOnlyCache<K, V> implements AsyncWriteCache<K, V, CacheItem<K, V>> {

    public AsyncReadAndWriteCache(PersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        super(vehicle, vehicle);
    }

    public AsyncReadAndWriteCache(PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle, List<PersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles) {
        super(writeVehicle, readVehicles);
    }

    @Override
    public void save() {
        Map<K, CacheItem<K, V>> map = new HashMap<>(nullCache);
        getWriteVehicle().save(getPlugin(), map);
    }

    @Override
    public void save(UUID partition) {
        Map<K, CacheItem<K, V>> map = new HashMap<>(localCache.get(partition));
        getWriteVehicle().save(getPlugin(), partition, map);
    }

    @Override
    public void putValue(K key, V value, @NotNull Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            CacheItem<K,V> cacheItem = nullCache.put(key, new CacheItem<>(
                    getWriteVehicle().getCodeAdapter().getType(), key, value,
                    0, ZonedDateTime.now()
            ));
            runTaskLater(() -> observers().forEach(observer -> observer.onPut(key, value)), 1);
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void putValue(UUID partition, K key, V value, @NotNull Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            localCache.putIfAbsent(partition, new HashMap<>());
            CacheItem<K,V> cacheItem = localCache.get(partition).put(key, new CacheItem<>(
                    getWriteVehicle().getCodeAdapter().getType(), key, value,
                    0, ZonedDateTime.now()
            ));
            runTaskLater(() -> observers().forEach(observer -> observer.onPut(partition, key, value)), 1);

            consumer.accept(cacheItem);
        });
    }

    @Override
    public void remove(K key, @NotNull Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            CacheItem<K, V> cacheItem = nullCache.remove(key);
            getWriteVehicle().delete(getPlugin(), null, cacheItem.getKey());
            runTaskLater(() -> observers().forEach(observer -> observer.onRemove(key, cacheItem.getData())), 1);
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void remove(UUID partition, K key, @NotNull Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            CacheItem<K, V> cacheItem = localCache.get(partition).remove(key);
            getWriteVehicle().delete(getPlugin(), partition, cacheItem.getKey());
            runTaskLater(() -> observers().forEach(observer -> observer.onRemove(partition, key, cacheItem.getData())), 1);
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void clear(UUID partition, @NotNull Consumer<List<CacheItem<K, V>>> consumer) {
        Map<K, CacheItem<K, V>> map = localCache.remove(partition);
        map.values().forEach(cacheItem ->
            getWriteVehicle().delete(getPlugin(), partition, cacheItem.getKey())
        );
        runTaskLater(() ->
            map.values().forEach(cacheItem ->
                runTaskLater(() -> observers().forEach(observer -> observer.onRemove(partition, cacheItem.getKey(), cacheItem.getData())), 1)
            )
        , 1);
        consumer.accept(new ArrayList<>(map.values()));
    }

    @Override
    public void removeBefore(ZonedDateTime zonedDateTime, @NotNull Consumer<List<CacheItem<K, V>>> consumer) {
        //TODO handle partitioned items
        runTaskAsync(() -> {
            List<K> itemsToRemove = new ArrayList<>();
            nullCache.forEach((key, cacheItem) -> {
                if (cacheItem.getUpdatedDate().isBefore(zonedDateTime)) {
                    itemsToRemove.add(key);
                }
            });
            List<CacheItem<K,V>> itemsRemoved = new ArrayList<>();
            itemsToRemove.forEach(key -> itemsRemoved.add(nullCache.remove(key)));
            consumer.accept(itemsRemoved);
        });
    }

    private Stream<WriteCacheOperationObserver<K,V>> observers() {
        return rOperationObservers.stream()
                .filter(observer -> observer instanceof WriteCacheOperationObserver<K,V>)
                .map(observer -> (WriteCacheOperationObserver<K,V>) observer);
    }
}
