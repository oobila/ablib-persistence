package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.MapOfConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;

@SuppressWarnings("unused")
public class SimpleFileCache<K, V> extends ReadAndWriteCache<K, V> {

    public SimpleFileCache(String pathString, Class<K> keyType, Class<V> valueType) {
        super(
                new DynamicVehicle<>(
                        pathString,
                        false,
                        keyType,
                        new FileStorageAdapter(".yml"),
                        new MapOfConfigurationSerializableCodeAdapter<>(valueType)
                )
        );
    }

}
