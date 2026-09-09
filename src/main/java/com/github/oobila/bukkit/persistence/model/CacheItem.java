package com.github.oobila.bukkit.persistence.model;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import lombok.Getter;

import java.time.ZonedDateTime;

/**
 * A single value held by a {@link com.github.oobila.bukkit.persistence.caches.Cache}, wrapping
 * its data together with its key and storage metadata. See
 * {@link com.github.oobila.bukkit.persistence.model.OnDemandCacheItem} for the lazily-loaded
 * subclass used by on-demand caches.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
@Getter
public class CacheItem<K, V> {

    private final K key;
    private final Class<V> type;
    protected V data;
    /** Size, in bytes/characters, of this item's serialized storage form. */
    private final long size;
    /** When this item was last written to storage, or {@code null} if never/unknown. */
    protected final ZonedDateTime updatedDate;

    public CacheItem(Class<V> type, K key, V data, StoredData storedData) {
        this(type, key, data, storedData.getSize(), storedData.getUpdatedDate());
    }

    public CacheItem(Class<V> type, K key, V data, long size, ZonedDateTime updatedDate) {
        this.type = type;
        this.key = key;
        this.data = data;
        this.size = size;
        this.updatedDate = updatedDate;
    }
}

