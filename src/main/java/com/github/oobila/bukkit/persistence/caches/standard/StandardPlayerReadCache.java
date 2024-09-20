package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.PlayerReadCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.util.Map;
import java.util.UUID;

public interface StandardPlayerReadCache<K, V> extends PlayerReadCache<K, V> {

    V get(UUID id, K key);

    CacheItem<K,V> getWithMetadata(UUID id, K key);

    Map<K, CacheItem<K,V>> getWithMetadata(UUID id);

    void loadPlayer(UUID id);

}