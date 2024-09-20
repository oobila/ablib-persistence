package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;

@SuppressWarnings("unused")
public interface StandardWriteCache<K, V>  extends WriteCache<K, V>, StandardReadCache<K, V> {

    CacheItem<K,V> put(K key, V value);

    CacheItem<K,V> remove(K key);

    List<CacheItem<K,V>> removeBefore(ZonedDateTime zonedDateTime);

}
