package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncWriteCache<K, V, C extends CacheItem<K, V>> extends WriteCache<K, V, C>, AsyncReadCache<K, V, C> {

    void putValue(K key, V value, @NotNull Consumer<C> consumer);

    void putValue(UUID partition, K key, V value, @NotNull Consumer<C> consumer);

    void remove(K key, @NotNull Consumer<C> consumer);

    void remove(UUID partition, K key, @NotNull Consumer<C> consumer);

    void clear(UUID partition, @NotNull Consumer<List<C>> consumer);

    void removeBefore(ZonedDateTime zonedDateTime, @NotNull Consumer<List<C>> consumer);

}
