package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.ConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.YamlMultiItemVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncReadAndWriteCache;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@SuppressWarnings("unused")
public class SimpleAsyncFileCache<K, V extends ConfigurationSerializable> extends AsyncReadAndWriteCache<K, V> {

    public SimpleAsyncFileCache(String name, Class<K> keyType, Class<V> valueType) {
        super(
                name,
                new YamlMultiItemVehicle<>(
                        keyType,
                        new FileStorageAdapter("yml"),
                        new ConfigurationSerializableCodeAdapter<>(valueType)
                )
        );
    }

}
