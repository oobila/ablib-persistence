package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncReadCache<K, V, C extends CacheItem<K, V>> extends ReadCache<K, V, C> {

    void getValue(K key, @NotNull Consumer<V> consumer);

    void getValue(UUID partition, K key, @NotNull Consumer<V> consumer);

    C get(UUID partition, K key);

    Collection<C> values();

    Collection<C> values(UUID partition);

    Collection<K> keySet();

    Collection<K> keySet(UUID partition);

}