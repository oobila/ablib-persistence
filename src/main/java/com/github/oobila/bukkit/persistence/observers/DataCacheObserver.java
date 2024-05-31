package com.github.oobila.bukkit.persistence.observers;

import com.github.oobila.bukkit.persistence.caches.DataCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;

public interface DataCacheObserver<K,V extends PersistedObject> {

    void onPut(K key, V value, DataCache<K, V> cache);
    void onRemove(K key, V value, DataCache<K, V> cache);
    void onOpen(DataCache<K, V> cache);
    void onClose(DataCache<K, V> cache);

}
