package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.function.Supplier;

public interface WriteCache<K, V, C extends CacheItem<K, V>> extends ReadCache<K, V, C> {

    void save();

    void save(UUID partition);

    default void transaction(UUID partition, Runnable runnable) {
        transaction(partition, () -> {
            runnable.run();
            return null;
        });
    }

    default void transaction(OfflinePlayer player, Runnable runnable) {
        transaction(player.getUniqueId(), runnable);
    }

    default V transaction(UUID partition, Supplier<V> supplier) {
        boolean loaded = isLoaded(partition);
        if (!loaded) {
            load(partition);
        }
        V v = supplier.get();
        if (!loaded) {
            save(partition);
            unload(partition);
        }
        return v;
    }

    default V transaction(OfflinePlayer player, Supplier<V> supplier) {
        return transaction(player.getUniqueId(), supplier);
    }

}
