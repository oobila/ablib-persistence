package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class ReadOnlyCache<K, V> implements StandardReadCache<K, V>, Map<K, CacheItem<K, V>> {

    @Setter(AccessLevel.PROTECTED)
    private Plugin plugin;
    private final List<PersistenceVehicle<K, V>> readVehicles;
    private final PersistenceVehicle<K, V> writeVehicle;

    @Delegate
    protected final Map<K, CacheItem<K, V>> nullCache = new HashMap<>();
    protected final Map<UUID, Map<K, CacheItem<K, V>>> localCache = new HashMap<>();


    public ReadOnlyCache(PersistenceVehicle<K, V> vehicle) {
        this(vehicle, vehicle);
    }

    public ReadOnlyCache(PersistenceVehicle<K, V> readVehicle, PersistenceVehicle<K, V> writeVehicle) {
        this(List.of(readVehicle), writeVehicle);
    }

    public ReadOnlyCache(List<PersistenceVehicle<K, V>> readVehicles, PersistenceVehicle<K, V> writeVehicle) {
        this.readVehicles = readVehicles;
        readVehicles.forEach(readVehicle -> readVehicle.setCache(this));
        this.writeVehicle = writeVehicle;
        writeVehicle.setCache(this);
        localCache.putIfAbsent(null, nullCache);
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
        clear();
        writeVehicle.copyDefaults();
        readVehicles.forEach(vehicle -> nullCache.putAll(vehicle.load(plugin)));
    }

    @Override
    public void load(UUID partition) {
        localCache.putIfAbsent(partition, new HashMap<>());
        Map<K, CacheItem<K, V>> map = localCache.get(partition);
        readVehicles.forEach(vehicle -> map.putAll(vehicle.load(plugin, partition)));
    }

    @Override
    public void unload() {
        nullCache.clear();
        localCache.clear();
        localCache.putIfAbsent(null, nullCache);
    }

    @Override
    public void unload(UUID partition) {
        localCache.remove(partition);
    }

    @Override
    @SneakyThrows
    public V getValue(K key) {
        return get(key).getData();
    }

    @Override
    @SneakyThrows
    public CacheItem<K, V> get(UUID partition, K key) {
        return localCache.get(partition).get(key);
    }

    @Override
    @SneakyThrows
    public V getValue(UUID partition, K key) {
        return get(partition, key).getData();
    }

}
