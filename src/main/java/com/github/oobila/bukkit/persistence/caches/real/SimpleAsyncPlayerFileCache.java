package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.adapters.vehicle.PlayerYamlConfigVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncPlayerReadAndWriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.observers.PlayerSaveObserver;

@SuppressWarnings("unused")
public class SimpleAsyncPlayerFileCache<K, V> extends AsyncPlayerReadAndWriteCache<K, V> {

    public SimpleAsyncPlayerFileCache(String name, Class<K> keyType, Class<V> valueType) {
        this(
                name,
                new PlayerYamlConfigVehicle<>(
                        keyType,
                        valueType,
                        new FileStorageAdapter("yml")
                )
        );
    }

    public SimpleAsyncPlayerFileCache(String name, PlayerPersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        super(name, vehicle);
        addPlayerObserver((PlayerSaveObserver<K, V, CacheItem<K, V>>) (playerId, savedData) -> unloadPlayer(playerId));
    }

}
