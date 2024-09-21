package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ReadOnlyCache<K, V> implements StandardReadCache<K, V>, Map<K, CacheItem<K,V>> {

    private Plugin plugin;
    private final String name;
    private final PersistenceVehicle<K, V> writeVehicle;
    private final List<PersistenceVehicle<K, V>> readVehicles;

    @Delegate
    protected final Map<K, CacheItem<K,V>> localCache = new HashMap<>();

    public ReadOnlyCache(String name, PersistenceVehicle<K, V> vehicle) {
        this( name, vehicle, vehicle);
    }

    public ReadOnlyCache(String name, PersistenceVehicle<K, V> writeVehicle,
                         PersistenceVehicle<K, V> readVehicle) {
        this(name, writeVehicle, List.of(readVehicle));
    }

    public ReadOnlyCache(String name, PersistenceVehicle<K, V> writeVehicle,
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
    public V getValue(K key) {
        return get(key).getData();
    }
}
