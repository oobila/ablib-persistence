package com.github.oobila.bukkit.persistence.caches.multi;

import com.github.oobila.bukkit.persistence.caches.IPlayerCache;
import com.github.oobila.bukkit.persistence.caches.PlayerCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class MultiPlayerCache<K, V extends PersistedObject> extends MultiCacheBase<K, V, PlayerCache<K, V>> implements IPlayerCache<K, V> {


    protected MultiPlayerCache(String name, Class<K> keyType, Class<V> valueType, PlayerCache<K, V> writer) {
        super(name, keyType, valueType, writer);
    }

    protected MultiPlayerCache(String name, Class<K> keyType, Class<V> valueType, PlayerCache<K, V> writer, String subFolderName) {
        super(name, keyType, valueType, writer, subFolderName);
    }

    @Override
    public void open(OfflinePlayer player) {
        cacheWriteInstance.open(player);
        cacheReadInstances.forEach(cache -> cache.open(player));
    }

    @Override
    public void put(OfflinePlayer player, K key, V value) {
        cacheWriteInstance.put(player, key, value);
    }

    @Override
    public V get(OfflinePlayer player, K key) {
        V v = null;
        if (canReadFromWriter) {
            v = cacheWriteInstance.get(player, key);
            if (v != null) {
                return v;
            }
        }
        for (IPlayerCache<K, V> cacheReader : cacheReadInstances) {
            v = cacheReader.get(player, key);
            if (v != null) {
                return v;
            }
        }
        return v;
    }

    @Override
    public List<V> get(OfflinePlayer player) {
        List<V> list = new ArrayList<>();
        if (canReadFromWriter) {
            list.addAll(cacheWriteInstance.get(player));
        }
        for (IPlayerCache<K, V> cacheReader : cacheReadInstances) {
            list.addAll(cacheReader.get(player));
        }
        return list;
    }

    @Override
    public void remove(OfflinePlayer offlinePlayer, K key) {
        cacheWriteInstance.remove(offlinePlayer, key);
    }

    @Override
    public void remove(OfflinePlayer offlinePlayer) {
        cacheWriteInstance.remove(offlinePlayer);
    }
}
