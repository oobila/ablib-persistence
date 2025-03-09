package com.github.oobila.bukkit.persistence.model;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class CacheItem<K, D> {

    private final K key;
    private final Class<D> type;
    protected D data;
    private final long size;
    protected final ZonedDateTime updatedDate;

    public CacheItem(Class<D> type, K key, D data, StoredData storedData) {
        this(type, key, data, storedData.getSize(), storedData.getUpdatedDate());
    }

    public CacheItem(Class<D> type, K key, D data, long size, ZonedDateTime updatedDate) {
        this.type = type;
        this.key = key;
        this.data = data;
        this.size = size;
        this.updatedDate = updatedDate;
    }
}

