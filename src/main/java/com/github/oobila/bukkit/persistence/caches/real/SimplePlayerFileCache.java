package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.ConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerYamlMultiItemVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.PlayerReadAndWriteCache;
import com.github.oobila.bukkit.persistence.observers.PlayerSaveObserver;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@SuppressWarnings("unused")
public class SimplePlayerFileCache<K, V extends ConfigurationSerializable> extends PlayerReadAndWriteCache<K, V> {

    public SimplePlayerFileCache(String name, Class<K> keyType, Class<V> valueType) {
        this(
                name,
                new PlayerYamlMultiItemVehicle<>(
                        keyType,
                        new FileStorageAdapter("yml"),
                        new ConfigurationSerializableCodeAdapter<>(valueType)
                )
        );
    }

    public SimplePlayerFileCache(String name, PlayerPersistenceVehicle<K, V> vehicle) {
        super(name, vehicle);
        addPlayerObserver((PlayerSaveObserver<K, V>) (playerId, savedData) -> unloadPlayer(playerId));
    }

}
