package com.github.oobila.bukkit.persistence.old.model;

import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.caches.Cache;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
public class OnDemandCacheItem<K, D> extends CacheItem<K, D> {

    private final Cache<K, D, OnDemandCacheItem<K, D>> cache;

    public OnDemandCacheItem(Class<D> type, K key, D data, StoredData storedData, Cache<K, D, OnDemandCacheItem<K, D>> cache) {
        super(type, key, data, storedData);
        this.cache = cache;
    }

    public OnDemandCacheItem(Class<D> type, K key, D data, long size, ZonedDateTime updatedDate, Cache<K, D, OnDemandCacheItem<K, D>> cache) {
        super(type, key, data, size, updatedDate);
        this.cache = cache;
    }

    public OnDemandCacheItem(Class<D> type, K key, D data, long size, ZonedDateTime updatedDate, UUID ownerId,
                             Cache<K, D, OnDemandCacheItem<K, D>> cache) {
        super(type, key, data, size, updatedDate, ownerId);
        this.cache = cache;
    }

    @Override
    public D getData() {
        if (data != null) {
            D temp = data;
            data = null;
            return temp;
        }

        CacheItem<K, D> cacheItem = ((ClusterPersistenceVehicle<K, D, OnDemandCacheItem<K, D>>) cache.getWriteVehicle()).loadSingle(
                cache.getPlugin(),
                cache.getName(),
                FilenameUtils.getBaseName(Serialization.serialize(getKey()))
        );
        return cacheItem.data;
    }

    public void unload() {
        this.data = null;
    }

    public void setData() {

    }
}

