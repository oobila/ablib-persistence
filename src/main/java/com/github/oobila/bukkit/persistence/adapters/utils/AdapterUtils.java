package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.adapters.DataCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataSqlAdapter;
import com.github.oobila.bukkit.persistence.adapters.PlayerCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataPlayerFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataPlayerSqlAdapter;
import com.github.oobila.bukkit.persistence.adapters.sql.YamlSqlAdapter;
import com.github.oobila.bukkit.persistence.caches.DataCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;

public class AdapterUtils {

    public static <K, V extends PersistedObject> DataCacheAdapter<K, V> adapter(StorageType storageType, DataCache<K, V> cache) {
        return switch (storageType) {
            case FILE -> new DataFileAdapter<>();
            case SQL -> new DataSqlAdapter<>(new YamlSqlAdapter<>(cache, cache.getType()));
        };
    }

    public static <K, V extends PersistedObject> PlayerCacheAdapter<K, V> playerAdapter(StorageType storageType, DataCache<K, V> cache) {
        return switch (storageType) {
            case FILE -> new DataPlayerFileAdapter<>();
            case SQL -> new DataPlayerSqlAdapter<>(new YamlSqlAdapter<>(cache, cache.getType()));
        };
    }

    public enum StorageType {
        FILE,
        SQL
    }

}
