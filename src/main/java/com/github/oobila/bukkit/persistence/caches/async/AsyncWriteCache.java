package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * The asynchronous read/write cache API: like {@code StandardWriteCache}, but every operation
 * that touches storage is dispatched off-thread and reports its result via a {@code Consumer}.
 */
@SuppressWarnings("unused")
public interface AsyncWriteCache<K, V, C extends CacheItem<K, V>> extends WriteCache<K, V, C>, AsyncReadCache<K, V, C> {

    /** Asynchronously stores {@code value} globally under {@code key}. */
    void putValue(K key, V value, @NotNull Consumer<C> consumer);

    /** Asynchronously stores {@code value} under {@code key} within {@code partition}. */
    void putValue(UUID partition, K key, V value, @NotNull Consumer<C> consumer);

    /** Asynchronously removes the globally-stored item under {@code key}. */
    void remove(K key, @NotNull Consumer<C> consumer);

    /** Asynchronously removes the item stored under {@code key} within {@code partition}. */
    void remove(UUID partition, K key, @NotNull Consumer<C> consumer);

    /** Asynchronously removes every item stored within {@code partition}. */
    void clear(UUID partition, @NotNull Consumer<List<C>> consumer);

    /** Asynchronously removes every globally-stored item last updated before {@code zonedDateTime}. */
    void removeBefore(ZonedDateTime zonedDateTime, @NotNull Consumer<List<C>> consumer);

}
