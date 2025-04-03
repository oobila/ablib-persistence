package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface ReadCache<K, V> extends Cache {

    PersistenceVehicle<K, V> getWriteVehicle(); // This is also used for plugins to write. I.e. copyDefaults

    List<PersistenceVehicle<K, V>> getReadVehicles();

    void load(Plugin plugin);

    void load(UUID partition);

    void unload(UUID partition);

    default String getPathString(){
        return getWriteVehicle().getPathString();
    }

}
