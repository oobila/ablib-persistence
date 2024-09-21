package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.PlayerReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings({"unused","java:S1845"})
public interface AsyncPlayerReadCache<K, V> extends PlayerReadCache<K, V> {

    void getValue(UUID id, K key, Consumer<V> consumer);

    void get(UUID id, K key, Consumer<CacheItem<K,V>> consumer);

    void get(UUID id, Consumer<Map<K, CacheItem<K,V>>> consumer);

    void loadPlayer(UUID id, Consumer<Map<K, CacheItem<K, V>>> consumer);

}