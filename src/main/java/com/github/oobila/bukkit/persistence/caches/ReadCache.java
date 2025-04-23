package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface ReadCache<K, V, C extends CacheItem<K, V>> extends Cache {

    PersistenceVehicle<K, V, C> getWriteVehicle(); // This is also used for plugins to write. I.e. copyDefaults

    List<PersistenceVehicle<K, V, C>> getReadVehicles();

    void load(Plugin plugin);

    void load(UUID partition);

    void unload();

    void unload(UUID partition);

    boolean isLoaded(UUID partition);

    default String getPathString(){
        return getWriteVehicle().getPathString();
    }

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
            unload(partition);
        }
        return v;
    }

    default V transaction(OfflinePlayer player, Supplier<V> supplier) {
        return transaction(player.getUniqueId(), supplier);
    }

}
