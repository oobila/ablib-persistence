package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.ClipboardCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.SqlStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncOnDemandCache;
import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;
import com.sk89q.worldedit.extent.clipboard.Clipboard;

/**
 * A SQL-backed, on-demand cache for WorldEdit {@link Clipboard}s (schematics) — e.g. for storing
 * many player-built structures without keeping every schematic resident in memory. Values are
 * (de)serialized with the Sponge Schematic v3 format via {@code ClipboardCodeAdapter}.
 *
 * @param <K> the key type identifying individual schematics
 */
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