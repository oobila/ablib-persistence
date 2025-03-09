package com.github.oobila.bukkit.persistence.old.caches.real;

import com.github.oobila.bukkit.persistence.old.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.old.vehicles.player.PlayerPersistenceVehicle;
import com.github.oobila.bukkit.persistence.old.vehicles.player.PlayerYamlConfigVehicle;
import com.github.oobila.bukkit.persistence.old.caches.standard.PlayerReadAndWriteCache;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.old.observers.PlayerSaveObserver;

@SuppressWarnings("unused")
public class SimplePlayerFileCache<K, V> extends PlayerReadAndWriteCache<K, V> {

    public SimplePlayerFileCache(String name, Class<K> keyType, Class<V> valueType) {
        this(
                name,
                new PlayerYamlConfigVehicle<>(
                        keyType,
                        valueType,
                        new FileStorageAdapter("yml")
                )
        );
    }

    public SimplePlayerFileCache(String name, PlayerPersistenceVehicle<K, V, CacheItem<K, V>> vehicle) {
        super(name, vehicle);
        addPlayerObserver((PlayerSaveObserver<K, V, CacheItem<K, V>>) (playerId, savedData) -> unloadPlayer(playerId));
    }

}
