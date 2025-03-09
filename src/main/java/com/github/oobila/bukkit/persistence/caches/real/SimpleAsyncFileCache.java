package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.YamlConfigVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncReadAndWriteCache;

@SuppressWarnings("unused")
public class SimpleAsyncFileCache<K, V> extends AsyncReadAndWriteCache<K, V> {

    public SimpleAsyncFileCache(String name, Class<K> keyType, Class<V> valueType) {
        super(
                name,
                new YamlConfigVehicle<>(
                        keyType,
                        valueType,
                        new FileStorageAdapter("yml")
                )
        );
    }

}
