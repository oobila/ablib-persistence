package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import org.bukkit.OfflinePlayer;

public interface PlayerCacheAdapter<K, V extends PersistedObject> {

    void open(BaseCache<K, V> playerCache);

    void open(OfflinePlayer player, BaseCache<K, V> playerCache);

    void close(BaseCache<K, V> playerCache);

    void close(OfflinePlayer player, BaseCache<K, V> playerCache);

    void put(OfflinePlayer player, K key, V value, BaseCache<K, V> playerCache);

    V get(OfflinePlayer player, K key, BaseCache<K, V> playerCache);

    void remove(OfflinePlayer player, K key, BaseCache<K, V> playerCache);

    void remove(OfflinePlayer player, BaseCache<K, V> playerCache);
}
