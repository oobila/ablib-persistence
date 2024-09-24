package com.github.oobila.bukkit.persistence.model;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Getter
public class CacheItem<K, D> {

    private final K key;
    private final D data;
    private final long size;
    private final ZonedDateTime updatedDate;

    public CacheItem(K key, D data, StoredData storedData) {
        this(key, data, storedData.getSize(), storedData.getUpdatedDate());
    }
}

