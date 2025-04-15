package com.github.oobila.bukkit.persistence.observers;

import java.util.UUID;

public interface ReadCacheOperationObserver<K, V> {

    void onLoad(K key, V value);

    void onLoad(UUID partition, K key, V value);

    void onUnload();

    void onUnload(UUID partition);

}
