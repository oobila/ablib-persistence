package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.ConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.SqlStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncOnDemandCache;
import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;
import org.bukkit.configuration.serialization.ConfigurationSerializable;


public class SimpleSqlCache<K, V extends ConfigurationSerializable> extends AsyncOnDemandCache<K, V> {

    public SimpleSqlCache(String pluginName, String tableName, Class<K> keyType, Class<V> valueType, SqlConnectionProperties connectionProperties) {
        super(
                new DynamicVehicle<>(
                        String.format("table=%s,p={uuid},k={key}", tableName),
                        true,
                        keyType,
                        new SqlStorageAdapter(pluginName, tableName, connectionProperties),
                        new ConfigurationSerializableCodeAdapter<>(valueType, false)
                )
        );
    }

}