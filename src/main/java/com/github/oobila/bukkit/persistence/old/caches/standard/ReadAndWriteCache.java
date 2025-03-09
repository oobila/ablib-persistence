package com.github.oobila.bukkit.persistence.old.caches.standard;

import com.github.oobila.bukkit.persistence.old.vehicles.global.GlobalPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Getter
public class ReadAndWriteCache<K, V> extends ReadOnlyCache<K, V> implements StandardWriteCache<K, V, CacheItem<K, V>> {

    public ReadAndWriteCache(String name, GlobalPersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        super(name, vehicle, vehicle);
    }

    public ReadAndWriteCache(
            String name,
            List<GlobalPersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles,
            GlobalPersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle
    ) {
        super(name, readVehicles, writeVehicle);
    }


    @Override
    public void save() {
        getWriteVehicle().save(getPlugin(), getName(), localCache);
    }

    @Override
    public CacheItem<K, V> putValue(K key, V value) {
        return localCache.put(key, new CacheItem<>(
                getWriteVehicle().getCodeAdapter().getType(), key, value, 0, ZonedDateTime.now()
        ));
    }

    @Override
    public List<CacheItem<K, V>> removeBefore(ZonedDateTime zonedDateTime) {
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
