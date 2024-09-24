package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.YamlConfigVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@SuppressWarnings("unused")
public class SimpleFileCache<K, V extends ConfigurationSerializable> extends ReadAndWriteCache<K, V> {

    public SimpleFileCache(String name, Class<K> keyType) {
        super(
                name,
                new YamlConfigVehicle<>(
                        keyType,
                        new FileStorageAdapter("yml")
                )
        );
    }

}
