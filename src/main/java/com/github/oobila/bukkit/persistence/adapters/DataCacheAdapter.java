package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;

import java.time.ZonedDateTime;

public interface DataCacheAdapter<K, V extends PersistedObject> {
    
    void open(BaseCache<K, V> dataCache);

    void close(BaseCache<K, V> dataCache);

    void put(K key, V value, BaseCache<K, V> dataCache);

    V get(K key, BaseCache<K, V> dataCache);

    void remove(K key, BaseCache<K, V> dataCache);

    int removeBefore(ZonedDateTime zonedDateTime, BaseCache<K, V> dataCache);
}
