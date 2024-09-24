package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncReadCache<K, V> extends ReadCache<K, V> {

    void getValue(K key, Consumer<V> consumer);

    void get(K key, Consumer<CacheItem<K,V>> consumer);

}