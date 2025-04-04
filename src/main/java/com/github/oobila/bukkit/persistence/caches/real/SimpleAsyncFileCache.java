package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.ConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncReadAndWriteCache;

@SuppressWarnings("unused")
public class SimpleAsyncFileCache<K, V> extends AsyncReadAndWriteCache<K, V> {

    public SimpleAsyncFileCache(String pathString, Class<K> keyType, Class<V> valueType) {
        super(
                new DynamicVehicle<>(
                        pathString,
                        false,
                        keyType,
                        new FileStorageAdapter(),
                        new ConfigurationSerializableCodeAdapter<>(valueType)
                )
        );
    }

}
