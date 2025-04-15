package com.github.oobila.bukkit.persistence.observers;

import java.util.UUID;

public interface WriteCacheOperationObserver<K, V> extends ReadCacheOperationObserver<K, V> {

    void onPut(K key, V value);

    void onPut(UUID partition, K key, V value);

    void onRemove(K key, V value);

    void onRemove(UUID partition, K key, V value);

}
