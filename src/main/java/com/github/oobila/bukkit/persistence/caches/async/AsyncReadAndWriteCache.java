package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@SuppressWarnings({"unused"})
@Getter
public class AsyncReadAndWriteCache<K, V> extends AsyncReadOnlyCache<K, V> implements AsyncWriteCache<K, V> {


    public AsyncReadAndWriteCache(PersistenceVehicle<K, V> vehicle) {
        super(vehicle, vehicle);
    }

    public AsyncReadAndWriteCache(PersistenceVehicle<K, V> writeVehicle, List<PersistenceVehicle<K, V>> readVehicles) {
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
        getWriteVehicle().save(getPlugin(), map);
    }

    @Override
    public void putValue(K key, V value, Consumer<OnDemandCacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            OnDemandCacheItem<K,V> cacheItem = nullCache.put(key, new OnDemandCacheItem<>(
                    getWriteVehicle().getCodeAdapter().getType(), null, key, value,
                    0, ZonedDateTime.now(), this
            ));
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void putValue(UUID partition, K key, V value, Consumer<OnDemandCacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            localCache.putIfAbsent(partition, new HashMap<>());
            OnDemandCacheItem<K,V> cacheItem = localCache.get(partition).put(key, new OnDemandCacheItem<>(
                    getWriteVehicle().getCodeAdapter().getType(), partition, key, value,
                    0, ZonedDateTime.now(), this
            ));
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void remove(K key, Consumer<OnDemandCacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            OnDemandCacheItem<K, V> cacheItem = nullCache.remove(key);
            cacheItem.delete();
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void remove(UUID partition, K key, Consumer<OnDemandCacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            OnDemandCacheItem<K, V> cacheItem = localCache.get(partition).remove(key);
            cacheItem.delete();
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void clear(UUID partition, Consumer<List<OnDemandCacheItem<K, V>>> consumer) {
        Map<K, OnDemandCacheItem<K, V>> map = localCache.remove(partition);
        map.values().forEach(OnDemandCacheItem::delete);
        consumer.accept(new ArrayList<>(map.values()));
    }

    @Override
    public void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<OnDemandCacheItem<K, V>>> consumer) {
        //TODO handle partitioned items
        runTaskAsync(() -> {
            List<K> itemsToRemove = new ArrayList<>();
            nullCache.forEach((key, cacheItem) -> {
                if (cacheItem.getUpdatedDate().isBefore(zonedDateTime)) {
                    itemsToRemove.add(key);
                }
            });
            List<OnDemandCacheItem<K,V>> itemsRemoved = new ArrayList<>();
            itemsToRemove.forEach(key -> itemsRemoved.add(nullCache.remove(key)));
            consumer.accept(itemsRemoved);
        });
    }
}
