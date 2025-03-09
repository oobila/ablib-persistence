package com.github.oobila.bukkit.persistence.old.caches.standard;

import com.github.oobila.bukkit.persistence.old.vehicles.player.OnDemandPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.vehicles.player.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.old.observers.PlayerObserver;
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
import java.util.UUID;

@SuppressWarnings("unused")
@Getter
public class OnDemandPlayerCache<K, V>
        implements StandardPlayerWriteCache<K, V, OnDemandCacheItem<K, V>>, Map<K, OnDemandCacheItem<K, V>> {

    private final String name;
    private Plugin plugin;
    private final OnDemandPersistenceVehicle<K, V, OnDemandCacheItem<K, V>> vehicle;

    @Delegate(excludes = Excludes.class)
    protected final Map<UUID, Map<K, OnDemandCacheItem<K, V>>> localCache = new HashMap<>();

    public OnDemandPlayerCache(String name, OnDemandPersistenceVehicle<K, V, OnDemandCacheItem<K, V>> clusterVehicle) {
        this.name = name;
        this.vehicle = clusterVehicle;
        vehicle.setCache(this);
    }

    @Override
    public PlayerPersistenceVehicle<K, V, OnDemandCacheItem<K, V>> getWriteVehicle() {
        return vehicle;
    }

    @Override
    public List<PlayerPersistenceVehicle<K, V, OnDemandCacheItem<K, V>>> getReadVehicles() {
        return List.of(vehicle);
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadPlayer(UUID id) {
        List<String> items = vehicle.getStorageAdapter().poll(plugin, name);
        for (String item : items) {
            OnDemandCacheItem<K, V> current = vehicle.loadMetadataSingle(plugin, name, item);
            localCache.put(current.getKey(), current);
        }
    }

    @Override
    public void unloadPlayer(UUID id) {
        localCache.get(id).clear();
    }

    @Override
    public void addPlayerObserver(PlayerObserver<K, V, OnDemandCacheItem<K, V>> playerObserver) {

    }

    @Override
    public void save() {
        //do nothing
    }

    @SuppressWarnings("unchecked")
    @Override
    public OnDemandCacheItem<K, V> remove(Object key) {
        vehicle.deleteSingle(getPlugin(), name, (K) key);
        return localCache.remove(key);
    }

    @Override
    public void clear() {
        Set<K> toClear = new HashSet<>(localCache.keySet());
        toClear.forEach(this::remove);
    }

    @Override
    public OnDemandCacheItem<K, V> putValue(UUID id, K key, V value) {
        OnDemandCacheItem<K, V> cacheItem = new OnDemandCacheItem<>(
                getWriteVehicle().getCodeAdapter().getType(), key, value, 0, ZonedDateTime.now(), id, this
        );
        vehicle.saveSingle(plugin, name, id, cacheItem);
        cacheItem.unload();
        localCache.putIfAbsent(id, new HashMap<>());
        localCache.get(id).put(key, cacheItem);
        return cacheItem;
    }

    @Override
    public OnDemandCacheItem<K, V> remove(UUID id, K key) {
        vehicle.deleteSingle(getPlugin(), name, id, key);
        return localCache.get(id).remove(key);
    }

    @Override
    public List<OnDemandCacheItem<K, V>> removeBefore(ZonedDateTime zonedDateTime) {
        List<K> itemsToRemove = new ArrayList<>();
        localCache.forEach((key, cacheItem) -> {
            if (cacheItem.getUpdatedDate().isBefore(zonedDateTime)) {
                itemsToRemove.add(key);
            }
        });
        List<OnDemandCacheItem<K, V>> itemsRemoved = new ArrayList<>();
        itemsToRemove.forEach(key -> itemsRemoved.add(remove(key)));
        return itemsRemoved;
    }

    @Override
    public void savePlayer(UUID id) {
        //do nothing
    }

    @Override
    public V getValue(UUID id, K key) {
        localCache.putIfAbsent(id, new HashMap<>());
        return localCache.get(id).get(key).getData();
    }

    @Override
    public OnDemandCacheItem<K, V> getWithMetadata(UUID id, K key) {
        return null;
    }

    @Override
    public Map<K, OnDemandCacheItem<K, V>> getWithMetadata(UUID id) {
        return null;
    }

    interface Excludes<K, V> {
        V remove(Object key);
        void clear();
    }
}
