package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.ClipboardCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.SqlStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncOnDemandCache;
import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;
import com.sk89q.worldedit.extent.clipboard.Clipboard;

public class ClipboardSqlCache<K> extends AsyncOnDemandCache<K, Clipboard> {

    public ClipboardSqlCache(String pluginName, String tableName, Class<K> keyType, SqlConnectionProperties connectionProperties) {
        super(
                new DynamicVehicle<>(
                        String.format("table=%s,p={uuid},k={key}", tableName),
                        true,
                        keyType,
                        new SqlStorageAdapter(pluginName, tableName, connectionProperties),
                        new ClipboardCodeAdapter()
                )
        );
    }

}