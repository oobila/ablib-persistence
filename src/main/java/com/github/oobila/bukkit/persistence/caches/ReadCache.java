package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.plugin.Plugin;

import java.util.List;

@SuppressWarnings("unused")
public interface ReadCache<K, V, C extends CacheItem<K, V>> extends Cache {

    PersistenceVehicle<K, V, C> getWriteVehicle(); // This is also used for plugins to write. I.e. copyDefaults

    List<PersistenceVehicle<K, V, C>> getReadVehicles();

    void load(Plugin plugin);

    void unload();

}
