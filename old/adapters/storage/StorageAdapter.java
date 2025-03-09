package com.github.oobila.bukkit.persistence.old.adapters.storage;

import org.bukkit.plugin.Plugin;

import java.util.List;

public interface StorageAdapter {


    List<String> poll(Plugin plugin, String path);

    boolean exists(Plugin plugin, String path);

    List<StoredData> readMetadata(Plugin plugin, String path);

    List<StoredData> read(Plugin plugin, String path);

    void write(Plugin plugin, String path, List<StoredData> storedDataList);

    void copyDefaults(Plugin plugin, String path);

    void delete(Plugin plugin, String path);

    String getExtension();

}
