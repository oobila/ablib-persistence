package com.github.oobila.bukkit.persistence.model;

import java.time.ZonedDateTime;

public class Resource<T> extends CacheItem<String, T> {
    public Resource(String key, T data, long size, ZonedDateTime updatedDate) {
        super(key, data, size, updatedDate);
    }
}
