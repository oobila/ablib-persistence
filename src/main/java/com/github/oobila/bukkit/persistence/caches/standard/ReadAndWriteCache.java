package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Getter
public class ReadAndWriteCache<K, V> extends ReadOnlyCache<K, V> implements StandardWriteCache<K, V> {

    public ReadAndWriteCache(String name, PersistenceVehicle<K, V> vehicle) {
        super(name, vehicle, vehicle);
    }

    public ReadAndWriteCache(String name, PersistenceVehicle<K, V> writeVehicle,
                             List<PersistenceVehicle<K, V>> readVehicles) {
        super(name, writeVehicle, readVehicles);
    }


    @Override
    public void save() {
        getWriteVehicle().save(getPlugin(), getName(), localCache);
    }

    @Override
    public CacheItem<K,V> put(K key, V value) {
        return localCache.put(key, new CacheItem<>(key, value, 0, ZonedDateTime.now()));
    }

    @Override
    public CacheItem<K,V> remove(K key) {
        return localCache.remove(key);
    }

    @Override
    public List<CacheItem<K,V>> removeBefore(ZonedDateTime zonedDateTime) {
        List<K> itemsToRemove = new ArrayList<>();
        localCache.forEach((key, cacheItem) -> {
            if (cacheItem.getUpdatedDate().isBefore(zonedDateTime)) {
                itemsToRemove.add(key);
            }
        });
        List<CacheItem<K,V>> itemsRemoved = new ArrayList<>();
        itemsToRemove.forEach(key -> itemsRemoved.add(localCache.remove(key)));
        return itemsRemoved;
    }
}
