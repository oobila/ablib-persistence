package com.github.oobila.bukkit.persistence.caches.async;

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
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@Getter
public class AsyncPlayerReadOnlyCache<K, V> implements AsyncPlayerReadCache<K, V> {

    private final Plugin plugin;
    private final String name;
    private final List<PlayerPersistenceVehicle<K, V>> readVehicles;
    protected final List<PlayerObserver<K, V>> playerObservers = new ArrayList<>();
    protected final Map<UUID, Map<K, CacheItem<K,V>>> localCache = new HashMap<>();

    public AsyncPlayerReadOnlyCache(Plugin plugin, String name, PlayerPersistenceVehicle<K, V> vehicle) {
        this(plugin, name, List.of(vehicle));
    }

    public AsyncPlayerReadOnlyCache(Plugin plugin, String name,List<PlayerPersistenceVehicle<K, V>> readVehicles) {
        this.plugin = plugin;
        this.name = name;
        this.readVehicles = readVehicles;
        CacheManager.register(this);
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
                getWithMetaData(id, consumer);
            }
            runTaskAsync(() ->
                    playerObservers.forEach(playerObserver -> {
                        if (playerObserver instanceof PlayerLoadObserver<K, V> playerLoadObserver) {
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
                        if (observer instanceof PlayerUnloadObserver<K,V> playerUnloadObserver) {
                            playerUnloadObserver.onUnload(id, unloadedData);
                        }
                    }
            ));
        });
    }

    @Override
    public void addPlayerObserver(PlayerObserver<K, V> playerObserver) {
        playerObservers.add(playerObserver);
    }

    @Override
    public void get(UUID id, K key, Consumer<V> consumer) {
        runTaskAsync(() -> {
            Map<K, CacheItem<K, V>> innerMap = localCache.get(id);
            CacheItem<K, V> cacheItem = innerMap == null ? null : innerMap.get(key);
            consumer.accept(Objects.requireNonNull(cacheItem).getData());
        });
    }

    @Override
    public void getWithMetadata(UUID id, K key, Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            Map<K, CacheItem<K, V>> innerMap = localCache.get(id);
            CacheItem<K, V> cacheItem = innerMap == null ? null : innerMap.get(key);
            consumer.accept(cacheItem);
        });
    }

    @Override
    public void getWithMetaData(UUID id, Consumer<Map<K, CacheItem<K, V>>> consumer) {
        runTaskAsync(() -> {
            Map<K, CacheItem<K,V>> map = localCache.get(id);
            consumer.accept(map);
        });
    }
}
