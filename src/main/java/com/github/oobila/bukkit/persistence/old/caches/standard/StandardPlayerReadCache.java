package com.github.oobila.bukkit.persistence.old.caches.standard;

import com.github.oobila.bukkit.persistence.old.caches.PlayerReadCache;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;

import java.util.Map;
import java.util.UUID;

public interface StandardPlayerReadCache<K, V, C extends CacheItem<K, V>> extends PlayerReadCache<K, V, C> {

    V getValue(UUID id, K key);

    C getWithMetadata(UUID id, K key);

    Map<K, C> getWithMetadata(UUID id);

    void loadPlayer(UUID id);

}