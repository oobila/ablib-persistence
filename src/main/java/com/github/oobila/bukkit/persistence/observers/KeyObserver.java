package com.github.oobila.bukkit.persistence.observers;

import com.github.oobila.bukkit.persistence.caches.DataCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.experimental.Delegate;

import java.util.ArrayList;
import java.util.List;

public abstract class KeyObserver<K> implements DataCacheKeyObserver<K>, DataCacheObserver<K, PersistedObject> {

    @Override
    public void onPut(K key, PersistedObject value, DataCache<K, PersistedObject> cache) {
        KeyObserver.this.onPut(key);
    }

    @Override
    public void onRemove(K key, PersistedObject value, DataCache<K, PersistedObject> cache) {
        KeyObserver.this.onRemove(key);
    }

    @Override
    public void onOpen(DataCache<K, PersistedObject> cache) {
        List<K> keyList = new ArrayList<>();
        cache.forEach((k, v) -> keyList.add(k));
        KeyObserver.this.onOpen(keyList);
    }

    @Override
    public void onClose(DataCache<K, PersistedObject> cache) {
        //no need to update this
    }
}
