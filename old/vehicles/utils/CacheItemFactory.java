package com.github.oobila.bukkit.persistence.old.vehicles.utils;

import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.caches.Cache;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.model.OnDemandCacheItem;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class CacheItemFactory<K, D, C extends CacheItem<K, D>> {

    private final boolean isOnDemand;
    @Setter
    private Cache<K, D, C> cache;

    public C newItem(Class<D> type, K key, D data, StoredData storedData) {
        return this.newItem(type, key, data, storedData.getSize(), storedData.getUpdatedDate(), storedData.getOwnerId());
    }

    public C newItem(Class<D> type, K key, D data, long size, ZonedDateTime updatedDate) {
        return this.newItem(type, key, data, size, updatedDate, null);
    }

    @SuppressWarnings("unchecked")
    public C newItem(Class<D> type, K key, D data, long size, ZonedDateTime updatedDate, UUID ownerId) {
        if (isOnDemand) {
            return (C) new OnDemandCacheItem<>(type, key, null, size, updatedDate, ownerId, (Cache<K, D, OnDemandCacheItem<K,D>>) cache);
        } else {
            return (C) new CacheItem<>(type, key, data, size, updatedDate, ownerId);
        }
    }
}
