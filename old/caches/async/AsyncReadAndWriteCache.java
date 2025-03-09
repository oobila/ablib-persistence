package com.github.oobila.bukkit.persistence.old.caches.async;

import com.github.oobila.bukkit.persistence.old.vehicles.global.GlobalPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@SuppressWarnings("unused")
@Getter
public class AsyncReadAndWriteCache<K, V>
        extends AsyncReadOnlyCache<K, V>
        implements AsyncWriteCache<K, V, CacheItem<K, V>> {


    public AsyncReadAndWriteCache(String name, GlobalPersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        super(name, vehicle, vehicle);
    }

    public AsyncReadAndWriteCache(String name, List<GlobalPersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles,
                                  GlobalPersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle) {
        super(name, writeVehicle, readVehicles);
    }


    @Override
    public void save() {
        getWriteVehicle().save(getPlugin(), getName(), localCache);
    }

    @Override
    public void putValue(K key, V value, Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            CacheItem<K,V> cacheItem = localCache.put(key, new CacheItem<>(
                    getWriteVehicle().getCodeAdapter().getType(), key, value, 0, ZonedDateTime.now()
            ));
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
