package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncWriteCache<K, V> extends WriteCache<K, V>, AsyncReadCache<K, V> {

    void putValue(K key, V value, Consumer<CacheItem<K,V>> consumer);

    void remove(K key, Consumer<CacheItem<K,V>> consumer);

    void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<CacheItem<K,V>>> consumer);

}
