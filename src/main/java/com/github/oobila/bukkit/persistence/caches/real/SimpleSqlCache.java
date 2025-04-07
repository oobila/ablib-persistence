package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.MapOfConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.SqlStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.SqlCache;
import com.github.oobila.bukkit.persistence.caches.async.AsyncReadAndWriteCache;
import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;
import lombok.Getter;

@Getter
public class SimpleSqlCache <K, V> extends AsyncReadAndWriteCache<K, V> implements SqlCache {

    private final SqlConnectionProperties sqlConnectionProperties;

    public SimpleSqlCache(String pluginName, String tableName, Class<K> keyType, Class<V> valueType, SqlConnectionProperties connectionProperties) {
        super(
                new DynamicVehicle<>(
                        String.format("table=%s;uuid={uuid};key={key}", tableName),
                        false,
                        keyType,
                        new SqlStorageAdapter(pluginName, tableName, connectionProperties),
                        new MapOfConfigurationSerializableCodeAdapter<>(valueType)
                )
        );
        this.sqlConnectionProperties = connectionProperties;
    }

}