package com.github.oobila.bukkit.persistence.old.vehicles.readmethod;

import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import org.bukkit.plugin.Plugin;

import java.util.List;

public interface PersistenceReadMethod {

    List<StoredData> read(StorageAdapter storageAdapter, Plugin plugin, String path);

}
