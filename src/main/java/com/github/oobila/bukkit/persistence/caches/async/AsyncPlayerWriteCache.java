package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.PlayerWriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncPlayerWriteCache<K, V> extends PlayerWriteCache<K, V>, AsyncPlayerReadCache<K, V> {

    void putValue(UUID id, K key, V value, Consumer<CacheItem<K,V>> consumer);

    void remove(UUID id, K key, Consumer<CacheItem<K,V>> consumer);

    void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<CacheItem<K,V>>> consumer);

    void savePlayer(UUID id, Consumer<Map<K, CacheItem<K, V>>> consumer);

}
