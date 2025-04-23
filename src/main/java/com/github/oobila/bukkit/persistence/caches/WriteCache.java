package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public interface WriteCache<K, V, C extends CacheItem<K, V>> extends ReadCache<K, V, C> {

    void save();

    void save(UUID partition);

    default void transaction(UUID partition, Runnable runnable) {
        boolean loaded = isLoaded(partition);
        if (!loaded) {
            load(partition);
        }
        runnable.run();
        if (!loaded) {
            save(partition);
            unload(partition);
        }
    }

    default void transaction(OfflinePlayer player, Runnable runnable) {
        transaction(player.getUniqueId(), runnable);
    }

}
