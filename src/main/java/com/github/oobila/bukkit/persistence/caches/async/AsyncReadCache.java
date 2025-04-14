package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncReadCache<K, V, C extends CacheItem<K, V>> extends ReadCache<K, V, C> {

    void getValue(K key, Consumer<V> consumer);

    void getValue(UUID partition, K key, Consumer<V> consumer);

    C get(UUID partition, K key);

    Collection<C> values();

    Collection<C> values(UUID partition);

    Collection<K> keys();

    Collection<K> keys(UUID partition);

}