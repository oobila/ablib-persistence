package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerYamlConfigVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.PlayerReadAndWriteCache;
import com.github.oobila.bukkit.persistence.observers.PlayerSaveObserver;

@SuppressWarnings("unused")
public class SimplePlayerFileCache<K, V> extends PlayerReadAndWriteCache<K, V> {

    public SimplePlayerFileCache(String name, Class<K> keyType) {
        this(
                name,
                new PlayerYamlConfigVehicle<>(
                        keyType,
                        new FileStorageAdapter("yml")
                )
        );
    }

    public SimplePlayerFileCache(String name, PlayerPersistenceVehicle<K, V> vehicle) {
        super(name, vehicle);
        addPlayerObserver((PlayerSaveObserver<K, V>) (playerId, savedData) -> unloadPlayer(playerId));
    }

}
