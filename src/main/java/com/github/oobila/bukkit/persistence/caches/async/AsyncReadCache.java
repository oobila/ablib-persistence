package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncReadCache<K, V> extends ReadCache<K, V> {

    void getValue(K key, Consumer<V> consumer);

    void getValue(UUID partition, K key, Consumer<V> consumer);

    OnDemandCacheItem<K, V> get(UUID partition, K key);

    Collection<OnDemandCacheItem<K, V>> values();

    Collection<OnDemandCacheItem<K, V>> values(UUID partition);

    Collection<K> keys();

    Collection<K> keys(UUID partition);

}