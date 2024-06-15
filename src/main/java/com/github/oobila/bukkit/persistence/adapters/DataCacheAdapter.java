package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public interface DataCacheAdapter<K, V extends PersistedObject> extends CacheReader {
    
    void open(BaseCache<K, V> dataCache);

    void close(BaseCache<K, V> dataCache);

    void put(K key, V value, BaseCache<K, V> dataCache);

    V get(K key, BaseCache<K, V> dataCache);

    List<V> get(BaseCache<K, V> dataCache);

    V remove(K key, BaseCache<K, V> dataCache);

    Collection<V> removeBefore(ZonedDateTime zonedDateTime, BaseCache<K, V> dataCache);

    int size(BaseCache<K, V> dataCache);
}
