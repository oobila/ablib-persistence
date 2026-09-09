package com.github.oobila.bukkit.persistence.model;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.caches.WriteCache;
import lombok.Getter;
import lombok.NonNull;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * A {@link CacheItem} for {@code caches.async.AsyncOnDemandCache}s that lazily reloads its value
 * from storage rather than keeping it in memory indefinitely. {@link #getData()} returns the
 * value it was constructed with exactly once, then clears it; every subsequent call re-fetches it
 * from the owning {@link #cache}'s write vehicle. This keeps large or rarely-accessed data (e.g.
 * every record in a SQL table) from being retained in memory just because it was read once — see
 * {@code AsyncOnDemandCache}, which calls {@link #unload()} automatically a short time after each
 * access.
 */
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

    /**
     * Returns this item's value. The first call returns whatever value this item was constructed
     * with (if any) and immediately clears the in-memory copy; every call after that (or if none
     * was supplied) re-reads the value from storage via the owning cache's write vehicle.
     */
    @Override
    public V getData() {
        if (data != null) {
            V temp = data;
            data = null;
            return temp;
        }
        return cache.getWriteVehicle().load(cache.getPlugin(), partition, getKey()).data;
    }

    /** Drops the in-memory value, if any, so the next {@link #getData()} call re-reads it from storage. */
    public void unload() {
        this.data = null;
    }

    /** Deletes this item from storage and clears its in-memory value. */
    public void delete() {
        cache.getWriteVehicle().delete(cache.getPlugin(), partition, getKey());
        data = null;
    }
}

