package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.ConfigurationSerializableCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.SqlStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncOnDemandCache;
import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;


/**
 * A SQL-backed, on-demand cache for {@code ConfigurationSerializable} values: rows are read/written
 * lazily and evicted from memory shortly after use (see {@link AsyncOnDemandCache}), making this
 * suitable for data sets far larger than could reasonably be held entirely in memory.
 *
 * @param <K> the key type identifying individual rows
 * @param <V> the value type being stored (must be {@code ConfigurationSerializable})
 */
public class SimpleSqlCache<K, V> extends AsyncOnDemandCache<K, V> {

    public SimpleSqlCache(String pluginName, String tableName, Class<K> keyType, Class<V> valueType, SqlConnectionProperties connectionProperties) {
        super(
                new DynamicVehicle<>(
                        String.format("table=%s,partition_id={uuid},record_key={key}", tableName),
                        true,
                        keyType,
                        new SqlStorageAdapter(pluginName, tableName, connectionProperties),
                        new ConfigurationSerializableCodeAdapter<>(valueType, false)
                )
        );
    }

}