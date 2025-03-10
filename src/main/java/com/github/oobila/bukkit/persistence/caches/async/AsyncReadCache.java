package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.Collection;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncReadCache<K, V, C extends CacheItem<K, V>> extends ReadCache<K, V, C> {

    void getValue(K key, Consumer<V> consumer);

    C get(K key);

    Collection<C> values();

    Collection<K> keys();

}