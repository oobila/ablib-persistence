package com.github.oobila.bukkit.persistence.old.caches.async;

import com.github.oobila.bukkit.persistence.old.caches.WriteCache;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncWriteCache<K, V, C extends CacheItem<K, V>> extends WriteCache<K, V, C>, AsyncReadCache<K, V, C> {

    void putValue(K key, V value, Consumer<C> consumer);

    void remove(K key, Consumer<C> consumer);

    void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<C>> consumer);

}
