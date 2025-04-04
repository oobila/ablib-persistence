package com.github.oobila.bukkit.persistence.adapters.storage;

import org.bukkit.plugin.Plugin;

import java.util.List;

public interface StorageAdapter {

    List<StoredData> read(Plugin plugin, String name);

    List<StoredData> readMetaData(Plugin plugin, String name);

    List<String> poll(Plugin plugin, String name);

    void write(Plugin plugin, String name, List<StoredData> storedDataList);

    void copyDefaults(Plugin plugin, String name);

    void delete(Plugin plugin, String name);

    boolean exists(Plugin plugin, String name);

}
