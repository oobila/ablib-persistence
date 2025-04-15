package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.caches.Cache;
import com.github.oobila.bukkit.persistence.model.BackwardsCompatibility;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public interface PersistenceVehicle<K, V, C extends CacheItem<K, V>> {

    void setPlugin(Plugin plugin);

    Cache getCache();

    String getPathString();

    void setCache(Cache cache);

    Map<K, C> load(Plugin plugin);

    Map<K, C> load(Plugin plugin, UUID partition);

    C load(Plugin plugin, UUID partition, K key);

    Collection<K> keys();

    Collection<K> keys(UUID partition);

    void copyDefaults();

    void save(Plugin plugin, Map<K, C> map);

    void save(Plugin plugin, UUID partition, Map<K, C> map);

    void save(Plugin plugin, UUID partition, K key, C cacheItem);

    void delete(Plugin plugin, UUID partition);

    void delete(Plugin plugin, UUID partition, K key);

    StorageAdapter getStorageAdapter();

    CodeAdapter<V> getCodeAdapter();

    Class<K> getKeyType();

    void addBackwardsCompatibility(BackwardsCompatibility backwardsCompatibility);

    List<BackwardsCompatibility> getBackwardsCompatibilityList();

}