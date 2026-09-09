package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * A cache that can load data into memory (and unload it again), keyed by type {@code K}, storing
 * values of type {@code V} wrapped in a {@code CacheItem} subtype {@code C}.
 * <p>
 * Data may be loaded globally ({@link #load(Plugin)}) or per-partition
 * ({@link #load(UUID)} — a partition is typically a player's UUID, but can be any UUID, e.g. a
 * world or arena). The {@link #transaction} helpers are a convenience for one-off access to a
 * partition that may not currently be loaded: they load it if necessary, run the given code, and
 * unload it again afterwards so short-lived access to offline players' data doesn't leak memory.
 *
 * @param <K> the key type identifying individual records within a partition
 * @param <V> the value type being stored
 * @param <C> the {@code CacheItem} subtype wrapping each stored value plus its metadata
 */
@SuppressWarnings("unused")
public interface ReadCache<K, V, C extends CacheItem<K, V>> extends Cache {

    // This is also used for plugins to write. I.e. copyDefaults
    /** The vehicle used to write this cache's data, and to read it back when there's only one source of truth. */
    PersistenceVehicle<K, V, C> getWriteVehicle();

    /** All vehicles data is read/merged from when loading (normally just the write vehicle). */
    List<PersistenceVehicle<K, V, C>> getReadVehicles();

    /** Loads this cache's global (unpartitioned) data for the given plugin. */
    void load(Plugin plugin);

    /** Loads this cache's data for a single partition (e.g. one player). */
    void load(UUID partition);

    /** Unloads all in-memory data, both global and every loaded partition. */
    void unload();

    /** Unloads the in-memory data for a single partition, freeing the memory it used. */
    void unload(UUID partition);

    /** Whether the given partition currently has its data loaded in memory. */
    boolean isLoaded(UUID partition);

    default String getPathString(){
        return getWriteVehicle().getPathString();
    }

    /** Runs {@code runnable} against {@code partition}'s data, loading/unloading it if it wasn't already loaded. */
    default void transaction(UUID partition, Runnable runnable) {
        transaction(partition, () -> {
            runnable.run();
            return null;
        });
    }

    /** Convenience overload of {@link #transaction(UUID, Runnable)} taking a player directly. */
    default void transaction(OfflinePlayer player, Runnable runnable) {
        transaction(player.getUniqueId(), runnable);
    }

    /**
     * Runs {@code supplier} against {@code partition}'s data and returns its result. If the
     * partition wasn't already loaded, it is loaded first and unloaded again afterwards, so
     * calling this on an offline player doesn't leave their data resident in memory.
     */
    default V transaction(UUID partition, Supplier<V> supplier) {
        boolean loaded = isLoaded(partition);
        if (!loaded) {
            load(partition);
        }
        V v = supplier.get();
        if (!loaded) {
            unload(partition);
        }
        return v;
    }

    /** Convenience overload of {@link #transaction(UUID, Supplier)} taking a player directly. */
    default V transaction(OfflinePlayer player, Supplier<V> supplier) {
        return transaction(player.getUniqueId(), supplier);
    }

}
