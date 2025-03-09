package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.caches.PlayerWriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public interface AsyncPlayerWriteCache<K, V, C extends CacheItem<K, V>>
        extends PlayerWriteCache<K, V, C>, AsyncPlayerReadCache<K, V, C> {

    void putValue(UUID id, K key, V value, Consumer<C> consumer);

    void remove(UUID id, K key, Consumer<C> consumer);

    void removeBefore(ZonedDateTime zonedDateTime, Consumer<List<C>> consumer);

    void savePlayer(UUID id, Consumer<Map<K, C>> consumer);

}
