package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerYamlConfigVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncPlayerReadAndWriteCache;
import com.github.oobila.bukkit.persistence.observers.PlayerSaveObserver;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@SuppressWarnings("unused")
public class SimpleAsyncPlayerFileCache<K, V extends ConfigurationSerializable> extends AsyncPlayerReadAndWriteCache<K, V> {

    public SimpleAsyncPlayerFileCache(String name, Class<K> keyType) {
        this(
                name,
                new PlayerYamlConfigVehicle<>(
                        keyType,
                        new FileStorageAdapter("yml")
                )
        );
    }

    public SimpleAsyncPlayerFileCache(String name, PlayerPersistenceVehicle<K, V> vehicle) {
        super(name, vehicle);
        addPlayerObserver((PlayerSaveObserver<K, V>) (playerId, savedData) -> unloadPlayer(playerId));
    }

}
