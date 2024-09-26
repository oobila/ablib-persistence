package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;

@SuppressWarnings("unused")
public interface StandardWriteCache<K, V, C extends CacheItem<K, V>>
        extends WriteCache<K, V, C>, StandardReadCache<K, V, C> {

    C putValue(K key, V value);

    List<C> removeBefore(ZonedDateTime zonedDateTime);

}
