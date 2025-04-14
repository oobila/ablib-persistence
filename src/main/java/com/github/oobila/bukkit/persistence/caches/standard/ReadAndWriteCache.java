package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    }

    @Override
    public void save(UUID partition) {
        getWriteVehicle().save(getPlugin(), localCache.get(partition));
    }

    @Override
    public CacheItem<K, V> putValue(K key, V value) {
        return nullCache.put(key, new CacheItem<>(
                getWriteVehicle().getCodeAdapter().getType(), key, value, 0, ZonedDateTime.now()
        ));
    }

    @Override
    public CacheItem<K, V> putValue(UUID partition, K key, V value) {
        localCache.putIfAbsent(partition, new HashMap<>());
        return localCache.get(partition).put(key, new CacheItem<>(
                getWriteVehicle().getCodeAdapter().getType(), key, value, 0, ZonedDateTime.now()
        ));
    }

    @Override
    public CacheItem<K, V> remove(UUID partition, K key) {
        return localCache.get(partition).remove(key);
    }

    @Override
    public List<CacheItem<K, V>> clear(UUID partition) {
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
        return itemsRemoved;
    }

    public void clear() {
        nullCache.clear();
        localCache.clear();
        localCache.putIfAbsent(null, nullCache);
    }

    @Delegate(excludes = Excludes.class)
    private Map<K, CacheItem<K, V>> getNullCacheDelegate() {
        return nullCache;
    }

    interface Excludes {
        void clear();
    }
}
