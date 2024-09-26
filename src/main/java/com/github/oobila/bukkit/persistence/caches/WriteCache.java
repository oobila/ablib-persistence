package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.model.CacheItem;

public interface WriteCache<K, V, C extends CacheItem<K, V>> extends ReadCache<K, V, C> {

    void save();

}
