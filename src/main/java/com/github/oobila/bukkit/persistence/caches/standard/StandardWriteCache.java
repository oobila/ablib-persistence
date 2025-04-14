package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface StandardWriteCache<K, V>
        extends WriteCache<K, V, CacheItem<K, V>>, StandardReadCache<K, V> {

    CacheItem<K, V> putValue(K key, V value);

    CacheItem<K, V> putValue(UUID partition, K key, V value);

    CacheItem<K, V> remove(Object key);

    CacheItem<K, V> remove(UUID partition, K key);

    List<CacheItem<K, V>> clear(UUID partition);

    List<CacheItem<K, V>> removeBefore(ZonedDateTime zonedDateTime);

}
