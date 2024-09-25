package com.github.oobila.bukkit.persistence.model;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class CacheItem<K, D> {

    private final K key;
    protected D data;
    private final long size;
    protected final ZonedDateTime updatedDate;

    public CacheItem(K key, D data, StoredData storedData) {
        this(key, data, storedData.getSize(), storedData.getUpdatedDate());
    }

    public CacheItem(K key, D data, long size, ZonedDateTime updatedDate) {
        this.key = key;
        this.data = data;
        this.size = size;
        this.updatedDate = updatedDate;
    }
}

