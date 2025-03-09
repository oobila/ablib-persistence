package com.github.oobila.bukkit.persistence.old.caches.async;

import com.github.oobila.bukkit.persistence.old.CacheManager;
import com.github.oobila.bukkit.persistence.old.vehicles.player.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.observers.PlayerLoadObserver;
import com.github.oobila.bukkit.persistence.old.observers.PlayerObserver;
import com.github.oobila.bukkit.persistence.old.observers.PlayerUnloadObserver;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@Getter
public class AsyncPlayerReadOnlyCache<K, V> implements AsyncPlayerReadCache<K, V, CacheItem<K, V>> {

    private Plugin plugin;
    private final String name;
    private final List<PlayerPersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles;
    protected final List<PlayerObserver<K, V, CacheItem<K, V>>> playerObservers = new ArrayList<>();
    protected final Map<UUID, Map<K, CacheItem<K,V>>> localCache = new HashMap<>();

    public AsyncPlayerReadOnlyCache(String name, PlayerPersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        this(name, List.of(vehicle));
    }

    public AsyncPlayerReadOnlyCache(String name,List<PlayerPersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles) {
        this.name = name;
        this.readVehicles = readVehicles;
        CacheManager.register(this);
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void loadPlayer(UUID id, Consumer<Map<K, CacheItem<K, V>>> consumer) {
        runTaskAsync(() -> {
            if (localCache.containsKey(id)) {
                localCache.get(id).clear();
            } else {
                localCache.put(id, new HashMap<>());
            }
            readVehicles.forEach(vehicle -> localCache.get(id).putAll(vehicle.loadPlayer(plugin, name, id)));
            if (consumer != null) {
                consumer.accept(values(id));
            }
            runTaskAsync(() ->
                    playerObservers.forEach(playerObserver -> {
                        if (playerObserver instanceof PlayerLoadObserver<K, V, CacheItem<K, V>> playerLoadObserver) {
                            playerLoadObserver.onLoad(id, localCache.get(id));
                        }
                    }
            ));
        });
    }

    @Override
    public void unloadPlayer(UUID id) {
        runTaskAsync(() -> {
            Map<K, CacheItem<K, V>> unloadedData = localCache.remove(id);
            runTaskAsync(() ->
                    playerObservers.forEach(observer -> {
                        if (observer instanceof PlayerUnloadObserver<K,V, CacheItem<K, V>> playerUnloadObserver) {
                            playerUnloadObserver.onUnload(id, unloadedData);
                        }
                    }
            ));
        });
    }

    @Override
    public void addPlayerObserver(PlayerObserver<K, V, CacheItem<K, V>> playerObserver) {
        playerObservers.add(playerObserver);
    }

    @Override
    public void getValue(UUID id, K key, Consumer<V> consumer) {
        runTaskAsync(() -> {
            Map<K, CacheItem<K, V>> innerMap = localCache.get(id);
            CacheItem<K, V> cacheItem = innerMap == null ? null : innerMap.get(key);
            consumer.accept(Objects.requireNonNull(cacheItem).getData());
        });
    }

    @Override
    public CacheItem<K, V> get(UUID id, K key) {
        Map<K, CacheItem<K, V>> innerMap = localCache.get(id);
        return innerMap == null ? null : innerMap.get(key);
    }

    @Override
    public Map<K, CacheItem<K, V>> values(UUID id) {
        return localCache.get(id);
    }
}
