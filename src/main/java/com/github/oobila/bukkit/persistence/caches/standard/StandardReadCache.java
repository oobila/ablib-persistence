package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

public interface StandardReadCache<K, V> extends ReadCache<K, V> {

    V get(K key);
    CacheItem<K,V> getWithMetadata(K key);

}