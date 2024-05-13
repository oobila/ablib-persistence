package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.caches.ConfigCache;

import java.time.LocalDateTime;
import java.util.List;

public interface ConfigCacheAdapter<K,V> extends CacheReader {

    void open(ConfigCache<K, V> configCache);

    V get(K key);

    List<K> keys();

    List<V> values();

    LocalDateTime getLastUpdated();
}