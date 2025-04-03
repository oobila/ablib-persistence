package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncWriteCache<K, V> extends WriteCache<K, V>, AsyncReadCache<K, V> {

    void putValue(K key, V value, Consumer<OnDemandCacheItem<K, V>> consumer);

    void putValue(UUID partition, K key, V value, Consumer<OnDemandCacheItem<K, V>> consumer);

    void remove(K key, Consumer<OnDemandCacheItem<K, V>> consumer);

    void remove(UUID partition, K key, Consumer<OnDemandCacheItem<K, V>> consumer);

    void clear(UUID partition, Consumer<List<OnDemandCacheItem<K, V>>> consumer);

    void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<OnDemandCacheItem<K, V>>> consumer);

}
