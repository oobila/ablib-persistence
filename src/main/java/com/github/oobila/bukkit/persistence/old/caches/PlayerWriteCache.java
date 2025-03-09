package com.github.oobila.bukkit.persistence.old.caches;

import com.github.oobila.bukkit.persistence.old.model.CacheItem;

@SuppressWarnings("unused")
public interface PlayerWriteCache<K, V, C extends CacheItem<K, V>> extends Cache<K, V, C> {

    void save();

}
