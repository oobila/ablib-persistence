package com.github.oobila.bukkit.persistence.model;

import java.time.ZonedDateTime;

public class Resource<T> extends CacheItem<String, T> {
    public Resource(Class<T> type, String key, T data, long size, ZonedDateTime updatedDate) {
        super(type, key, data, size, updatedDate);
    }
}
