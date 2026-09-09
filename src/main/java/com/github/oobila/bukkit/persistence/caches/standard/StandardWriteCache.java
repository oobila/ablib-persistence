package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * The synchronous read/write cache API; see
 * {@link com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache} for the
 * implementation. Changes made via {@code putValue}/{@code remove} only affect the in-memory
 * copy — call {@code save()}/{@code save(UUID)} (from {@link com.github.oobila.bukkit.persistence.caches.WriteCache})
 * to persist them.
 */
@SuppressWarnings("unused")
public interface StandardWriteCache<K, V>
        extends WriteCache<K, V, CacheItem<K, V>>, StandardReadCache<K, V> {

    /** Stores {@code value} globally under {@code key}, returning the item previously there (if any). */
    CacheItem<K, V> putValue(K key, V value);

    /** Stores {@code value} under {@code key} within {@code partition}, returning the item previously there (if any). */
    CacheItem<K, V> putValue(UUID partition, K key, V value);

    /** Removes and returns the globally-stored item under {@code key}. */
    CacheItem<K, V> remove(Object key);

    /** Removes and returns the item stored under {@code key} within {@code partition}. */
    CacheItem<K, V> remove(UUID partition, K key);

    /** Removes and returns every item stored within {@code partition}, deleting the partition's storage. */
    List<CacheItem<K, V>> clear(UUID partition);

    /** Removes and returns every globally-stored item last updated before {@code zonedDateTime}. */
    List<CacheItem<K, V>> removeBefore(ZonedDateTime zonedDateTime);

}
