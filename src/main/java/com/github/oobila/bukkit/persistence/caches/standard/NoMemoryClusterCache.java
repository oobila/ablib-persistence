package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.NoMemoryClusterVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.model.NoMemoryCacheItem;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.plugin.Plugin;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.oobila.bukkit.persistence.adapters.utils.DirectoryUtils.append;

@SuppressWarnings("unused")
@Getter
public class NoMemoryClusterCache<K, V> implements StandardWriteCache<K, V>, Map<K, NoMemoryCacheItem<K,V>> {

    private final String name;
    private Plugin plugin;
    private final NoMemoryClusterVehicle<K, V> vehicle;

    @Delegate(excludes = Excludes.class)
    protected final Map<K, NoMemoryCacheItem<K,V>> localCache = new HashMap<>();

    public NoMemoryClusterCache(String name, Class<K> keyType, StorageAdapter storageAdapter, CodeAdapter<V> codeAdapter) {
        this.name = name;
        this.vehicle = new NoMemoryClusterVehicle<>(keyType, storageAdapter, codeAdapter);
    }

    @Override
    public PersistenceVehicle<K, V> getWriteVehicle() {
        return vehicle;
    }

    @Override
    public List<PersistenceVehicle<K, V>> getReadVehicles() {
        return List.of(vehicle);
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
        List<String> items = vehicle.getStorageAdapter().poll(plugin, name);
        for (String item : items) {
            NoMemoryCacheItem<K, V> current = vehicle.loadMetadataSingle(plugin, append(name, item));
            localCache.put(current.getKey(), current);
        }
    }

    @Override
    public void unload() {
        localCache.clear();
    }

    @Override
    public void save() {
        //do nothing
    }

    @Override
    public V getValue(K key) {
        return localCache.get(key).getData();
    }

    public void unloadValue(K key) {
        localCache.get(key).unload();
    }

    @SuppressWarnings("unchecked")
    @Override
    public NoMemoryCacheItem<K, V> remove(Object key) {
        vehicle.deleteSingle(getPlugin(), name, (K) key);
        return localCache.remove(key);
    }

    @Override
    public void clear() {
        Set<K> toClear = new HashSet<>(localCache.keySet());
        toClear.forEach(this::remove);
    }

    @Override
    public CacheItem<K, V> putValue(K key, V value) {
        NoMemoryCacheItem<K, V> cacheItem = new NoMemoryCacheItem<>(key, value, 0, ZonedDateTime.now(), this);
        vehicle.saveSingle(plugin, name, cacheItem);
        cacheItem.unload();
        localCache.put(key, cacheItem);
        return cacheItem;
    }

    @Override
    public List<CacheItem<K, V>> removeBefore(ZonedDateTime zonedDateTime) {
        List<K> itemsToRemove = new ArrayList<>();
        localCache.forEach((key, cacheItem) -> {
            if (cacheItem.getUpdatedDate().isBefore(zonedDateTime)) {
                itemsToRemove.add(key);
            }
        });
        List<CacheItem<K,V>> itemsRemoved = new ArrayList<>();
        itemsToRemove.forEach(key -> itemsRemoved.add(remove(key)));
        return itemsRemoved;
    }

    interface Excludes<K, V> {
        V remove(Object key);
        void clear();
    }
}
