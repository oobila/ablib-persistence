package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@SuppressWarnings("unused")
@Getter
public class AsyncReadOnlyCache<K, V> implements AsyncReadCache<K, V> {

    private Plugin plugin;
    private final String name;
    private final PersistenceVehicle<K, V> writeVehicle;
    private final List<PersistenceVehicle<K, V>> readVehicles;
    protected final Map<K, CacheItem<K,V>> localCache = new HashMap<>();

    public AsyncReadOnlyCache(String name, PersistenceVehicle<K, V> vehicle) {
        this(name, vehicle, vehicle);
    }

    public AsyncReadOnlyCache(String name, PersistenceVehicle<K, V> writeVehicle,
                              PersistenceVehicle<K, V> readVehicle) {
        this(name, writeVehicle, List.of(readVehicle));
    }

    public AsyncReadOnlyCache(String name, PersistenceVehicle<K, V> writeVehicle,
                              List<PersistenceVehicle<K, V>> readVehicles) {
        this.name = name;
        this.writeVehicle = writeVehicle;
        this.readVehicles = readVehicles;
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
        unload();
        if (plugin.getResource(name) != null && !writeVehicle.getStorageAdapter().exists(plugin, name)) {
            writeVehicle.getStorageAdapter().copyDefaults(plugin, name);
        }
        readVehicles.forEach(vehicle -> localCache.putAll(vehicle.load(plugin, name)));
    }

    @Override
    public void unload() {
        localCache.clear();
    }

    @Override
    public void get(K key, Consumer<V> consumer) {
        runTaskAsync(() -> {
            V value = localCache.get(key).getData();
            consumer.accept(value);
        });
    }

    @Override
    public void getWithMetadata(K key, Consumer<CacheItem<K, V>> consumer) {
        runTaskAsync(() -> {
            CacheItem<K, V> cacheItem = localCache.get(key);
            consumer.accept(cacheItem);
        });
    }
}
