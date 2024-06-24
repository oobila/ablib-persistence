package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.model.PersistedObject;
import org.bukkit.OfflinePlayer;

import java.util.List;

public interface IPlayerCache<K, V extends PersistedObject> extends ICache {

    void open(OfflinePlayer player);
    void close(OfflinePlayer player);
    void put(OfflinePlayer player, K key, V value);
    V get(OfflinePlayer player, K key);
    List<V> get(OfflinePlayer player);
    void remove(OfflinePlayer offlinePlayer, K key);
    void remove(OfflinePlayer offlinePlayer);

}
