package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.observers.CacheObserver;
import com.github.oobila.bukkit.persistence.observers.WriteCacheOperationObserver;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@Getter
public class ReadAndWriteCache<K, V> extends ReadOnlyCache<K, V> implements StandardWriteCache<K, V>, Map<K, CacheItem<K, V>> {

    public ReadAndWriteCache(PersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        super(vehicle, vehicle);
    }

    public ReadAndWriteCache(PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle, List<PersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles) {
        super(writeVehicle, readVehicles);
    }

    @Override
    public void save() {
        if (localCache.size() > 1) {
            localCache.forEach((uuid, map) -> {
                if (uuid == null) {
                    if (!map.isEmpty()) {
                        getWriteVehicle().save(getPlugin(), nullCache);
                    }
                } else {
                    getWriteVehicle().save(getPlugin(), uuid, map);
                }
            });
        } else {
            getWriteVehicle().save(getPlugin(), nullCache);
        }
        cacheObservers.stream().filter(CacheObserver.class::isInstance)
                .map(CacheObserver.class::cast).forEach(CacheObserver::onCacheSave);
    }

    @Override
    public void save(UUID partition) {
        getWriteVehicle().save(getPlugin(), partition, localCache.get(partition));
    }

    @Override
    public CacheItem<K, V> putValue(K key, V value) {
        CacheItem<K, V> cacheItem = nullCache.put(key, new CacheItem<>(
                getWriteVehicle().getCodeAdapter().getType(), key, value, 0, ZonedDateTime.now()
        ));
        observers().forEach(observer -> observer.onPut(key, value));
        return cacheItem;
    }

    @Override
    public CacheItem<K, V> putValue(UUID partition, K key, V value) {
        localCache.putIfAbsent(partition, new HashMap<>());
        CacheItem<K, V> cacheItem = localCache.get(partition).put(key, new CacheItem<>(
                getWriteVehicle().getCodeAdapter().getType(), key, value, 0, ZonedDateTime.now()
        ));
        observers().forEach(observer -> observer.onPut(partition, key, value));
        return cacheItem;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CacheItem<K, V> remove(Object key) {
        CacheItem<K, V> cacheItem = nullCache.remove(key);
        observers().forEach(observer -> observer.onRemove((K) key, cacheItem.getData()));
        return cacheItem;
    }

    @Override
    public CacheItem<K, V> remove(UUID partition, K key) {
        CacheItem<K, V> cacheItem = localCache.get(partition).remove(key);
        observers().forEach(observer -> observer.onRemove(partition, key, cacheItem.getData()));
        return cacheItem;
    }

    public void clear() {
        nullCache.forEach((k, cacheItem) ->
                observers().forEach(observer -> observer.onRemove(k, cacheItem.getData()))
        );
        localCache.forEach((uuid, kCacheItemMap) ->
            kCacheItemMap.forEach((k, cacheItem) -> {
                if (k != null) {
                    observers().forEach(observer -> observer.onRemove(uuid, k, cacheItem.getData()));
                }
            })
        );
        nullCache.clear();
        localCache.clear();
        localCache.putIfAbsent(null, nullCache);
    }

    @Override
    public List<CacheItem<K, V>> clear(UUID partition) {
        localCache.forEach((uuid, kCacheItemMap) ->
                kCacheItemMap.forEach((k, cacheItem) -> {
                    if (k != null) {
                        observers().forEach(observer -> observer.onRemove(uuid, k, cacheItem.getData()));
                    }
                })
        );
        List<CacheItem<K, V>> list = new ArrayList<>(localCache.get(partition).values());
        getWriteVehicle().delete(getPlugin(), partition);
        return list;
    }

    @Override
    public List<CacheItem<K, V>> removeBefore(ZonedDateTime zonedDateTime) {
        //TODO handle partitioned items
        List<K> itemsToRemove = new ArrayList<>();
        nullCache.forEach((key, cacheItem) -> {
            if (cacheItem.getUpdatedDate().isBefore(zonedDateTime)) {
                itemsToRemove.add(key);
            }
        });
        List<CacheItem<K,V>> itemsRemoved = new ArrayList<>();
        itemsToRemove.forEach(key -> itemsRemoved.add(nullCache.remove(key)));
        itemsRemoved.forEach(cacheItem ->
                observers().forEach(observer -> observer.onRemove(cacheItem.getKey(), cacheItem.getData()))
        );
        return itemsRemoved;
    }

    private Stream<WriteCacheOperationObserver<K,V>> observers() {
        return rOperationObservers.stream()
                .filter(observer -> observer instanceof WriteCacheOperationObserver<K,V>)
                .map(observer -> (WriteCacheOperationObserver<K,V>) observer);
    }

    @Delegate(excludes = Excludes.class)
    private Map<K, CacheItem<K, V>> getNullCacheDelegate() {
        return nullCache;
    }

    interface Excludes<K, V> {
        void clear();
        CacheItem<K, V> remove(Object key);
    }
}
