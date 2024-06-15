package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.adapters.DataCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataSqlAdapter;
import com.github.oobila.bukkit.persistence.adapters.PlayerCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.PlayerFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.PlayerSqlAdapter;
import com.github.oobila.bukkit.persistence.model.PersistedObject;

public class AdapterUtils {

    public static <K, V extends PersistedObject> DataCacheAdapter<K, V> adapter(StorageType storageType) {
        return switch (storageType) {
            case FILE -> new DataFileAdapter<>();
            case SQL -> new DataSqlAdapter<>();
        };
    }

    public static <K, V extends PersistedObject> PlayerCacheAdapter<K, V> playerAdapter(StorageType storageType) {
        return switch (storageType) {
            case FILE -> new PlayerFileAdapter<>();
            case SQL -> new PlayerSqlAdapter<>();
        };
    }

    public enum StorageType {
        FILE,
        SQL
    }

}
