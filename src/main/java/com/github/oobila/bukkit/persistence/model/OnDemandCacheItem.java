package com.github.oobila.bukkit.persistence.model;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.caches.WriteCache;
import lombok.Getter;
import lombok.NonNull;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@SuppressWarnings("unused")
public class OnDemandCacheItem<K, V> extends CacheItem<K, V> {

    private final WriteCache<K, V, OnDemandCacheItem<K, V>> cache;
    private final UUID partition;

    public OnDemandCacheItem(Class<V> type, UUID partition, K key, V data, StoredData storedData, @NonNull WriteCache<K, V, OnDemandCacheItem<K, V>> cache) {
        super(type, key, data, storedData);
        this.partition = partition;
        this.cache = cache;
    }

    public OnDemandCacheItem(Class<V> type, UUID partition, K key, V data, long size, ZonedDateTime updatedDate, @NonNull WriteCache<K, V, OnDemandCacheItem<K, V>> cache) {
        super(type, key, data, size, updatedDate);
        this.partition = partition;
        this.cache = cache;
    }

    @Override
    public V getData() {
        if (data != null) {
            V temp = data;
            data = null;
            return temp;
        }
        return cache.getWriteVehicle().load(cache.getPlugin(), partition, getKey()).data;
    }

    public void unload() {
        this.data = null;
    }

    public void delete() {
        cache.getWriteVehicle().delete(cache.getPlugin(), partition, getKey());
        data = null;
    }
}

