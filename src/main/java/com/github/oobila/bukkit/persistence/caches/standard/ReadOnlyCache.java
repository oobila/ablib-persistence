package com.github.oobila.bukkit.persistence.caches.standard;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
public class ReadOnlyCache<K, V> implements StandardReadCache<K, V> {

    @Setter(AccessLevel.PROTECTED)
    private Plugin plugin;
    private final List<PersistenceVehicle<K, V>> readVehicles;
    private final PersistenceVehicle<K, V> writeVehicle;

    protected final Map<K, CacheItem<K, V>> nullCache = new HashMap<>();
    protected final Map<UUID, Map<K, CacheItem<K, V>>> localCache = new HashMap<>();


    public ReadOnlyCache(PersistenceVehicle<K, V> vehicle) {
        this(vehicle, vehicle);
    }

    public ReadOnlyCache(PersistenceVehicle<K, V> writeVehicle, PersistenceVehicle<K, V> readVehicle) {
        this(writeVehicle, List.of(readVehicle));
    }

    public ReadOnlyCache(PersistenceVehicle<K, V> writeVehicle, List<PersistenceVehicle<K, V>> readVehicles) {
        this.readVehicles = readVehicles;
        readVehicles.forEach(readVehicle -> readVehicle.setCache(this));
        this.writeVehicle = writeVehicle;
        writeVehicle.setCache(this);
        localCache.putIfAbsent(null, nullCache);
    }

    @Override
    public void load(Plugin plugin) {
        this.plugin = plugin;
        writeVehicle.setPlugin(plugin);
        unload();
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

    public int size() {
        return Math.max(nullCache.size(), localCache.size() - 1);
    }

    public boolean isEmpty() {
        return nullCache.isEmpty() && localCache.size() < 2;
    }

    @SuppressWarnings({"unchecked", "java:S1905"})
    public boolean containsKey(Object key) {
        return nullCache.containsKey((K) key);
    }

    @SuppressWarnings({"all"})
    public boolean containsValue(Object value) {
        return nullCache.containsValue(value);
    }

    @SuppressWarnings({"unchecked", "java:S1905"})
    public CacheItem<K, V> get(Object key) {
        return nullCache.get((K) key);
    }

    @NotNull
    public Set<K> keySet() {
        return nullCache.keySet();
    }

    @NotNull
    public Collection<CacheItem<K, V>> values() {
        return nullCache.values();
    }

    @NotNull
    public Set<Map.Entry<K, CacheItem<K, V>>> entrySet() {
        return nullCache.entrySet();
    }

}
