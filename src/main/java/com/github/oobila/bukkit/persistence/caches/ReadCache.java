package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface ReadCache<K, V, C extends CacheItem<K, V>> extends Cache {

    PersistenceVehicle<K, V, C> getWriteVehicle(); // This is also used for plugins to write. I.e. copyDefaults

    List<PersistenceVehicle<K, V, C>> getReadVehicles();

    void load(Plugin plugin);

    void load(UUID partition);

    void unload();

    void unload(UUID partition);

    default String getPathString(){
        return getWriteVehicle().getPathString();
    }

}
