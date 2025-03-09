package com.github.oobila.bukkit.persistence.old.caches;

import com.github.oobila.bukkit.persistence.old.vehicles.global.GlobalPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import org.bukkit.plugin.Plugin;

import java.util.List;

public interface Cache<K, V, C extends CacheItem<K, V>> {

    String getName();

    Plugin getPlugin();

    GlobalPersistenceVehicle<K, V, C> getWriteVehicle(); // This is also used for plugins to write. I.e. copyDefaults

    List<GlobalPersistenceVehicle<K, V, C>> getReadVehicles();

    void load(Plugin plugin);

    void unload();

}
