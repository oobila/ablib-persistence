package com.github.oobila.bukkit.persistence.old.caches;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;

public interface WriteCache<K, V, C extends CacheItem<K, V>> extends ReadCache<K, V, C> {

    void save();

}
