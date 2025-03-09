package com.github.oobila.bukkit.persistence.repository.cache;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface Cache<K, V> {

    V get(UUID partition, K key);

    void put(UUID partition, K key, V value);

    void remove(UUID partition, K key);

    void unload(UUID partition);

    Map<K, V> getPartition(UUID partition);

    void putPartition(UUID partition, Map<K, V> map);

    Set<UUID> getLoadedPartitions();

}
