package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ReadOnlyCache<K, V> implements StandardReadCache<K, V> {

    private final Plugin plugin;
    private final String name;
    private final PersistenceVehicle<K, V> writeVehicle;
    private final List<PersistenceVehicle<K, V>> readVehicles;
    protected final Map<K, CacheItem<K,V>> localCache = new HashMap<>();

    public ReadOnlyCache(Plugin plugin, String name, PersistenceVehicle<K, V> vehicle) {
        this(plugin, name, vehicle, vehicle);
    }

    public ReadOnlyCache(Plugin plugin, String name, PersistenceVehicle<K, V> writeVehicle,
                         PersistenceVehicle<K, V> readVehicle) {
        this(plugin, name, writeVehicle, List.of(readVehicle));
    }

    public ReadOnlyCache(Plugin plugin, String name, PersistenceVehicle<K, V> writeVehicle,
                         List<PersistenceVehicle<K, V>> readVehicles) {
        this.plugin = plugin;
        this.name = name;
        this.writeVehicle = writeVehicle;
        this.readVehicles = readVehicles;
    }

    @Override
    public void load() {
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
    public V get(K key) {
        return getWithMetadata(key).getData();
    }

    @Override
    public CacheItem<K, V> getWithMetadata(K key) {
        return localCache.get(key);
    }
}
