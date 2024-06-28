package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.caches.DataCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
public class PlayerFileAdapter<K, V extends PersistedObject> implements PlayerCacheAdapter<K, V> {

    private final Map<UUID, DataCache<K, V>> localCache = new HashMap<>();
    private DataFileAdapter<K, V> fileAdapter = new DataFileAdapter<>();

    public PlayerFileAdapter(DataFileAdapter<K, V> fileAdapter) {
        this.fileAdapter = fileAdapter;
    }

    @Override
    public void open(BaseCache<K, V> cache) {
        //do nothing
    }

    @Override
    public void open(OfflinePlayer offlinePlayer, BaseCache<K, V> cache) {
        DataCache<K, V> dataCache = localCache.computeIfAbsent(
                offlinePlayer.getUniqueId(), uuid -> new DataCache<>(
                        cache.getName(),
                        cache.getKeyType(),
                        cache.getType(),
                        fileAdapter,
                        "playerdata/" + offlinePlayer.getUniqueId()
                )
        );
        dataCache.open(cache.getPlugin());
    }

    @Override
    public void close(BaseCache<K, V> playerCache) {
        Bukkit.getOnlinePlayers().forEach(player -> close(player, playerCache));
    }

    @Override
    public void close(OfflinePlayer player, BaseCache<K, V> cache) {
        localCache.get(player.getUniqueId()).close();
    }

    @Override
    public void put(OfflinePlayer player, K key, V value, BaseCache<K,V> playerCache) {
        localCache.get(player.getUniqueId()).put(key, value);
    }

    @Override
    public V get(OfflinePlayer player, K key, BaseCache<K,V> playerCache) {
        if (localCache.get(player.getUniqueId()) != null) {
            return localCache.get(player.getUniqueId()).get(key);
        }
        return null;
    }

    @Override
    public List<V> get(OfflinePlayer player, BaseCache<K, V> playerCache) {
        if (localCache.get(player.getUniqueId()) != null) {
            return localCache.get(player.getUniqueId()).get();
        }
        return Collections.emptyList();
    }

    @Override
    public void remove(OfflinePlayer player, K key, BaseCache<K,V> playerCache) {
        if (localCache.get(player.getUniqueId()) != null) {
            localCache.get(player.getUniqueId()).remove(key);
        }
    }

    @Override
    public void remove(OfflinePlayer player, BaseCache<K,V> playerCache) {
        localCache.remove(player.getUniqueId());
    }
}
