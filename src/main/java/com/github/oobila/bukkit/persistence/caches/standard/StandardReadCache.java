package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

public interface StandardReadCache<K, V, C extends CacheItem<K, V>> extends ReadCache<K, V, C> {

    V getValue(K key);

}