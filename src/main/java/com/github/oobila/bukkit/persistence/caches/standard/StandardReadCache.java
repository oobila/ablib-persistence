package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.ReadCache;

public interface StandardReadCache<K, V> extends ReadCache<K, V> {

    V getValue(K key);

}