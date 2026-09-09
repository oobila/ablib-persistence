package com.github.oobila.bukkit.persistence.observers;

import java.util.UUID;

/** Notified for individual record loads and for partition/global unloads. Register via a cache's {@code addObserver(...)}. */
public interface ReadCacheOperationObserver<K, V> {

    /** Called once per record as global (unpartitioned) data finishes loading. */
    void onLoad(K key, V value);

    /** Called once per record as a partition finishes loading. */
    void onLoad(UUID partition, K key, V value);

    void onUnload();

    void onUnload(UUID partition);

}
