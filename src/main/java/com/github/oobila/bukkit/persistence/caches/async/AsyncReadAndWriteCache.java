package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@SuppressWarnings("unused")
@Getter
public class AsyncReadAndWriteCache<K, V> extends AsyncReadOnlyCache<K, V> implements AsyncWriteCache<K, V> {


    public AsyncReadAndWriteCache(String name, PersistenceVehicle<K, V> vehicle) {
        super(name, vehicle, vehicle);
    }

    public AsyncReadAndWriteCache(String name, List<PersistenceVehicle<K, V>> readVehicles,
                                  PersistenceVehicle<K, V> writeVehicle) {
        super(name, writeVehicle, readVehicles);
    }


    @Override
    public void save() {
        getWriteVehicle().save(getPlugin(), getName(), localCache);
    }

    @Override
    public void put(K key, V value, Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            CacheItem<K,V> cacheItem = localCache.put(key, new CacheItem<>(key, value, 0, ZonedDateTime.now()));
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void remove(K key, Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            CacheItem<K, V> cacheItem = localCache.remove(key);
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<CacheItem<K, V>>> consumer) {
        runTaskAsync(() -> {
            List<K> itemsToRemove = new ArrayList<>();
            localCache.forEach((key, cacheItem) -> {
                if (cacheItem.getUpdatedDate().isBefore(zonedDateTime)) {
                    itemsToRemove.add(key);
                }
            });
            List<CacheItem<K,V>> itemsRemoved = new ArrayList<>();
            itemsToRemove.forEach(key -> itemsRemoved.add(localCache.remove(key)));
            consumer.accept(itemsRemoved);
        });
    }
}
