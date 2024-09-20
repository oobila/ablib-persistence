package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.observers.PlayerObserver;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public interface PlayerReadCache<K, V> extends Cache {

    List<PlayerPersistenceVehicle<K, V>> getReadVehicles();

    void unloadPlayer(UUID id);

    void addPlayerObserver(PlayerObserver<K, V> playerObserver);

}
