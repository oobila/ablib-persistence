package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.ConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.YamlMultiItemVehicle;
import com.github.oobila.bukkit.persistence.caches.standard.ReadAndWriteCache;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("unused")
public class SimpleFileCache<K, V extends ConfigurationSerializable> extends ReadAndWriteCache<K, V> {

    public SimpleFileCache(Plugin plugin, String name, Class<K> keyType, Class<V> valueType) {
        super(
                plugin,
                name,
                new YamlMultiItemVehicle<>(
                        keyType,
                        new FileStorageAdapter("yml"),
                        new ConfigurationSerializableCodeAdapter<>(valueType)
                )
        );
    }

}
