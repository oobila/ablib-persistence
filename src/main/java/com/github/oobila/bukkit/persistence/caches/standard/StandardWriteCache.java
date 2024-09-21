package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;

@SuppressWarnings("unused")
public interface StandardWriteCache<K, V>  extends WriteCache<K, V>, StandardReadCache<K, V> {

    CacheItem<K,V> putValue(K key, V value);

    List<CacheItem<K,V>> removeBefore(ZonedDateTime zonedDateTime);

}
