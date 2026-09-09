package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * The asynchronous counterpart of {@code StandardReadCache}: reads are delivered via a
 * {@code Consumer} callback (typically invoked off the main server thread) rather than returned
 * directly, so callers don't block on I/O.
 */
@SuppressWarnings("unused")
public interface AsyncReadCache<K, V, C extends CacheItem<K, V>> extends ReadCache<K, V, C> {

    /** Asynchronously fetches the globally-stored value under {@code key} and passes it to {@code consumer}. */
    void getValue(K key, @NotNull Consumer<V> consumer);

    /** Asynchronously fetches the value stored under {@code key} within {@code partition} and passes it to {@code consumer}. */
    void getValue(UUID partition, K key, @NotNull Consumer<V> consumer);

    C get(UUID partition, K key);

    Collection<C> values();

    Collection<C> values(UUID partition);

    Collection<K> keySet();

    Collection<K> keySet(UUID partition);

}