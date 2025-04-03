package com.github.oobila.bukkit.persistence.model;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class CacheItem<K, V> {

    private final K key;
    private final Class<V> type;
    protected V data;
    private final long size;
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

