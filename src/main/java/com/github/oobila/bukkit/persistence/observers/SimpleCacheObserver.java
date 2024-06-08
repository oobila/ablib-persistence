package com.github.oobila.bukkit.persistence.observers;

import com.github.oobila.bukkit.persistence.caches.DataCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;

public interface SimpleCacheObserver<K, V extends PersistedObject> extends DataCacheObserver<K, V> {

    default void onOpen(DataCache<K, V> cache) {
        cache.forEach((k, v) -> onPut(k, v, cache));
    }

    default void onClose(DataCache<K, V> cache) {
        //do nothing
    }
}
