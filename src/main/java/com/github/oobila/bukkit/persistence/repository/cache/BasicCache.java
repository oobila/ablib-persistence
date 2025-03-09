package com.github.oobila.bukkit.persistence.repository.cache;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


@RequiredArgsConstructor
public class BasicCache<K, V> implements Cache<K, V> {

    private final Map<UUID , Map<K, V>> map = new HashMap<>();


    @Override
    public V get(UUID partition, K key) {
        if (!map.containsKey(partition)) {
            return null;
        }
        return map.get(partition).get(key);
    }

    @Override
    public void put(UUID partition, K key, V value) {
        map.putIfAbsent(partition, new HashMap<>());
        map.get(partition).put(key, value);
    }

    @Override
    public void remove(UUID partition, K key) {
        map.putIfAbsent(partition, new HashMap<>());
        map.get(partition).remove(key);
    }

    @Override
    public void unload(UUID partition) {
        map.remove(partition);
    }

    @Override
    public Map<K, V> getPartition(UUID partition) {
        return map.get(partition);
    }

    @Override
    public void putPartition(UUID partition, Map<K, V> map) {
        this.map.put(partition, map);
    }

    @Override
    public Set<UUID> getLoadedPartitions() {
        return map.keySet();
    }
}
