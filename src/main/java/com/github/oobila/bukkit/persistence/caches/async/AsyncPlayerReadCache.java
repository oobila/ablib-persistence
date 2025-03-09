package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.PlayerReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings({"unused","java:S1845"})
public interface AsyncPlayerReadCache<K, V, C extends CacheItem<K, V>> extends PlayerReadCache<K, V, C> {

    void getValue(UUID id, K key, Consumer<V> consumer);

    C get(UUID id, K key);

    Map<K, C> values(UUID id);

    void loadPlayer(UUID id, Consumer<Map<K, C>> consumer);



}