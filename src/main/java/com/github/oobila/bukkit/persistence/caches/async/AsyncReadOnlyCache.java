package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.oobila.bukkit.common.ABCommon.runTaskAsync;

@SuppressWarnings("unused")
@Getter
public class AsyncReadOnlyCache<K, V> implements AsyncReadCache<K, V, CacheItem<K, V>> {

    private Plugin plugin;
    private final String name;
    private final PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle;
    private final List<PersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles;
    protected final Map<K, CacheItem<K,V>> localCache = new HashMap<>();

    public AsyncReadOnlyCache(String name, PersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        this(name, vehicle, vehicle);
    }

    public AsyncReadOnlyCache(String name, PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle,
                              PersistenceVehicle<K, V, CacheItem<K, V>> readVehicle) {
        this(name, writeVehicle, List.of(readVehicle));
    }

    public AsyncReadOnlyCache(String name, PersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle,
                              List<PersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles) {
        this.name = name;
        this.writeVehicle = writeVehicle;
        this.readVehicles = readVehicles;
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
        unload();
        if (resourceExists(plugin) && !writeVehicle.getStorageAdapter().exists(plugin, name)) {
            writeVehicle.getStorageAdapter().copyDefaults(plugin, name);
        }
        readVehicles.forEach(vehicle -> localCache.putAll(vehicle.load(plugin, name)));
    }

    private boolean resourceExists(Plugin plugin) {
        return plugin.getResource(String.format(
                "%s.%s",
                name,
                writeVehicle.getStorageAdapter().getExtension()
        )) != null;
    }

    @Override
    public void unload() {
        localCache.clear();
    }

    @Override
    public void getValue(K key, Consumer<V> consumer) {
        runTaskAsync(() -> {
            V value = localCache.get(key).getData();
            consumer.accept(value);
        });
    }

    @Override
    public CacheItem<K, V> get(K key) {
        return localCache.get(key);
    }

    @Override
    public Collection<CacheItem<K, V>> values() {
        return localCache.values();
    }
}
