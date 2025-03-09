package com.github.oobila.bukkit.persistence.old.caches.standard;

import com.github.oobila.bukkit.persistence.old.vehicles.global.GlobalPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ReadOnlyCache<K, V> implements StandardReadCache<K, V, CacheItem<K, V>>, Map<K, CacheItem<K, V>> {

    private Plugin plugin;
    private final String name;
    private final List<GlobalPersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles;
    private final GlobalPersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle;

    @Delegate
    protected final Map<K, CacheItem<K, V>> localCache = new HashMap<>();

    public ReadOnlyCache(String name, GlobalPersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        this( name, vehicle, vehicle);
    }

    public ReadOnlyCache(
            String name,
            GlobalPersistenceVehicle<K, V, CacheItem<K, V>> readVehicle,
            GlobalPersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle
    ) {
        this(name, List.of(readVehicle), writeVehicle);
    }

    public ReadOnlyCache(
            String name,
            List<GlobalPersistenceVehicle<K, V, CacheItem<K, V>>> readVehicles,
            GlobalPersistenceVehicle<K, V, CacheItem<K, V>> writeVehicle
    ) {
        this.name = name;
        this.readVehicles = readVehicles;
        readVehicles.forEach(readVehicle -> readVehicle.setCache(this));
        this.writeVehicle = writeVehicle;
        writeVehicle.setCache(this);
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
    public V getValue(K key) {
        return get(key).getData();
    }
}
