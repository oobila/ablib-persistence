package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.observers.PlayerObserver;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface PlayerReadCache<K, V, C extends CacheItem<K, V>> extends Cache {

    List<PlayerPersistenceVehicle<K, V, C>> getReadVehicles();

    void load(Plugin plugin);

    void unloadPlayer(UUID id);

    void addPlayerObserver(PlayerObserver<K, V, C> playerObserver);

}
