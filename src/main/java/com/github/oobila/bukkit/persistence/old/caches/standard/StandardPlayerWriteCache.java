package com.github.oobila.bukkit.persistence.old.caches.standard;

import com.github.oobila.bukkit.persistence.old.caches.PlayerWriteCache;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface StandardPlayerWriteCache<K, V, C extends CacheItem<K, V>>
        extends PlayerWriteCache<K, V, C>, StandardPlayerReadCache<K, V, C> {

    C putValue(UUID id, K key, V value);

    C remove(UUID id, K key);

    List<C> removeBefore(ZonedDateTime zonedDateTime);

    void savePlayer(UUID id);

}
