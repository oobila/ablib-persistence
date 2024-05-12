package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;

public interface ResourceCacheAdapter<K, V extends PersistedObject> {

    void open(BaseCache<K, V> dataCache);

    void close(BaseCache<K, V> dataCache);

    //TODO

}
