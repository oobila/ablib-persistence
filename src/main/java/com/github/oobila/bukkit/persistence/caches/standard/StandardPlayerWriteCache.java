package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.caches.PlayerWriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface StandardPlayerWriteCache<K, V>  extends PlayerWriteCache<K, V>, StandardPlayerReadCache<K, V> {

    CacheItem<K,V> putValue(UUID id, K key, V value);

    CacheItem<K,V> remove(UUID id, K key);

    List<CacheItem<K,V>> removeBefore(ZonedDateTime zonedDateTime);

    void savePlayer(UUID id);

}
