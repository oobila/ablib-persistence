package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * A {@link ReadCache} that can also persist its in-memory data back to storage. Adds a
 * {@code transaction} overload that saves the partition (in addition to loading/unloading it) when
 * it wasn't already loaded, so a one-off write to an offline player's data is safely flushed to
 * disk/SQL before the memory is freed.
 *
 * @param <K> the key type identifying individual records within a partition
 * @param <V> the value type being stored
 * @param <C> the {@code CacheItem} subtype wrapping each stored value plus its metadata
 */
public interface WriteCache<K, V, C extends CacheItem<K, V>> extends ReadCache<K, V, C> {

    /** Saves all currently-loaded global and per-partition data back to storage. */
    void save();

    /** Saves a single partition's currently-loaded data back to storage. */
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

    /**
     * Runs {@code supplier} against {@code partition}'s data and returns its result. If the
     * partition wasn't already loaded, it is loaded first, then saved and unloaded again
     * afterwards — the difference from {@link ReadCache#transaction(UUID, Supplier)} being that
     * any changes made by {@code supplier} are persisted before the data is dropped from memory.
     */
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
