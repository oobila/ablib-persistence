package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.UUID;

public interface StandardReadCache<K, V> extends ReadCache<K, V, CacheItem<K, V>> {

    V getValue(K key);

    V getValue(UUID partition, K key);

    CacheItem<K, V> get(UUID partition, K key);

}