package com.github.oobila.bukkit.persistence.model;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.adapters.vehicle.ClusterPersistenceVehicle;
import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.time.ZonedDateTime;

@Getter
public class OnDemandCacheItem<K, D> extends CacheItem<K, D> {

    private final WriteCache<K, D> cache;

    public OnDemandCacheItem(K key, D data, StoredData storedData, WriteCache<K, D> cache) {
        super(key, data, storedData);
        this.cache = cache;
    }

    public OnDemandCacheItem(K key, D data, long size, ZonedDateTime updatedDate, WriteCache<K, D> cache) {
        super(key, data, size, updatedDate);
        this.cache = cache;
    }

    @Override
    public D getData() {
        if (data != null) {
            D temp = data;
            data = null;
            return temp;
        }

        CacheItem<K, D> cacheItem = ((ClusterPersistenceVehicle<K, D>) cache.getWriteVehicle()).loadSingle(
                cache.getPlugin(),
                cache.getName(),
                FilenameUtils.getBaseName(Serialization.serialize(getKey()))
        );
        return cacheItem.data;
    }

    public void unload() {
        this.data = null;
    }
}

