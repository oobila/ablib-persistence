package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.MapOfConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncReadAndWriteCache;

/**
 * The asynchronous equivalent of {@link SimpleFileCache}: same YAML file storage of
 * {@code ConfigurationSerializable} values, but reads/writes are dispatched off the main thread.
 *
 * @param <K> the key type identifying individual records
 * @param <V> the value type being stored (must be {@code ConfigurationSerializable})
 */
@SuppressWarnings("unused")
public class SimpleAsyncFileCache<K, V> extends AsyncReadAndWriteCache<K, V> {

    public SimpleAsyncFileCache(String pathString, Class<K> keyType, Class<V> valueType) {
        super(
                new DynamicVehicle<>(
                        pathString,
                        false,
                        keyType,
                        new FileStorageAdapter(),
                        new MapOfConfigurationSerializableCodeAdapter<>(valueType)
                )
        );
    }

}
