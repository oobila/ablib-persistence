package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.Collection;
import java.util.UUID;

/**
 * A synchronous, always-in-memory-once-loaded read API: plain {@code CacheItem} wrapping (no
 * lazy-loading, unlike the {@code async.AsyncOnDemandCache} family), with direct return values
 * rather than callbacks. See {@link com.github.oobila.bukkit.persistence.caches.standard.ReadOnlyCache}
 * for the implementation.
 */
public interface StandardReadCache<K, V> extends ReadCache<K, V, CacheItem<K, V>> {

    /** The value stored globally under {@code key}. */
    V getValue(K key);

    /** The value stored under {@code key} within {@code partition}. */
    V getValue(UUID partition, K key);

    CacheItem<K, V> get(UUID partition, K key);

    /** All globally-stored values currently loaded. */
    Collection<CacheItem<K, V>> values();

    /** All values currently loaded for {@code partition}. */
    Collection<CacheItem<K, V>> values(UUID partition);

    /** Keys of all globally-stored values currently loaded. */
    Collection<K> keySet();

    /** Keys of all values currently loaded for {@code partition}. */
    Collection<K> keySet(UUID partition);

}