package com.github.oobila.bukkit.persistence.old.caches.real;

import com.github.oobila.bukkit.persistence.old.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.old.vehicles.global.YamlConfigVehicle;
import com.github.oobila.bukkit.persistence.old.caches.standard.ReadAndWriteCache;

@SuppressWarnings("unused")
public class SimpleFileCache<K, V> extends ReadAndWriteCache<K, V> {

    public SimpleFileCache(String name, Class<K> keyType, Class<V> valueType) {
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
