package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.ConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerYamlMultiItemVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncPlayerReadAndWriteCache;
import com.github.oobila.bukkit.persistence.observers.PlayerSaveObserver;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class SimpleAsyncPlayerFileCache<K, V extends ConfigurationSerializable> extends AsyncPlayerReadAndWriteCache<K, V> {

    public SimpleAsyncPlayerFileCache(Plugin plugin, String name, Class<K> keyType, Class<V> valueType) {
        this(
                plugin,
                name,
                new PlayerYamlMultiItemVehicle<>(
                        keyType,
                        new FileStorageAdapter("yml"),
                        new ConfigurationSerializableCodeAdapter<>(valueType)
                )
        );
    }

    public SimpleAsyncPlayerFileCache(Plugin plugin, String name, PlayerPersistenceVehicle<K, V> vehicle) {
        super(plugin, name, vehicle);
        addPlayerObserver((PlayerSaveObserver<K, V>) (playerId, savedData) -> unloadPlayer(playerId));
    }

}
