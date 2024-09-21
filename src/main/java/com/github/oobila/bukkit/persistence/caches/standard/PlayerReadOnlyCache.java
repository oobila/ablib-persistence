package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.CacheManager;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.observers.PlayerLoadObserver;
import com.github.oobila.bukkit.persistence.observers.PlayerObserver;
import com.github.oobila.bukkit.persistence.observers.PlayerUnloadObserver;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerReadOnlyCache<K, V> implements StandardPlayerReadCache<K, V> {

    private Plugin plugin;
    private final String name;
    private final List<PlayerPersistenceVehicle<K, V>> readVehicles;
    protected final List<PlayerObserver<K, V>> playerObservers = new ArrayList<>();
    protected final Map<UUID, Map<K, CacheItem<K,V>>> localCache = new HashMap<>();

    public PlayerReadOnlyCache(String name, PlayerPersistenceVehicle<K, V> vehicle) {
        this(name, List.of(vehicle));
    }

    public PlayerReadOnlyCache(String name, List<PlayerPersistenceVehicle<K, V>> readVehicles) {
        this.name = name;
        this.readVehicles = readVehicles;
        CacheManager.register(this);
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadPlayer(UUID id) {
        if (localCache.containsKey(id)) {
            localCache.get(id).clear();
        } else {
            localCache.put(id, new HashMap<>());
        }
        readVehicles.forEach(vehicle -> localCache.get(id).putAll(vehicle.loadPlayer(plugin, name, id)));
        playerObservers.forEach(observer -> {
            if (observer instanceof PlayerLoadObserver<K,V> playerLoadObserver) {
                playerLoadObserver.onLoad(id, localCache.get(id));
            }
        });
    }

    @Override
    public void unloadPlayer(UUID id) {
        Map<K, CacheItem<K, V>> unloadedData = localCache.remove(id);
        playerObservers.forEach(observer -> {
            if (observer instanceof PlayerUnloadObserver<K,V> playerUnloadObserver) {
                playerUnloadObserver.onUnload(id, unloadedData);
            }
        });
    }

    @Override
    public void addPlayerObserver(PlayerObserver<K, V> playerObserver) {
        playerObservers.add(playerObserver);
    }

    @Override
    public V get(UUID id, K key) {
        return getWithMetadata(id, key).getData();
    }

    @Override
    public CacheItem<K, V> getWithMetadata(UUID id, K key) {
        Map<K, CacheItem<K, V>> innerMap = getWithMetadata(id);
        return innerMap == null ? null : innerMap.get(key);
    }

    @Override
    public Map<K, CacheItem<K,V>> getWithMetadata(UUID id) {
        return localCache.get(id);
    }

}
