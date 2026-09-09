package com.github.oobila.bukkit.persistence.model;

import java.time.ZonedDateTime;

/** A single entry within a {@link ResourcePack}: one typed value keyed by its zip entry name. */
public class Resource<T> extends CacheItem<String, T> {
    public Resource(Class<T> type, String key, T data, long size, ZonedDateTime updatedDate) {
        super(type, key, data, size, updatedDate);
    }
}
