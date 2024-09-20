package com.github.oobila.bukkit.persistence.adapters.storage;

import org.bukkit.plugin.Plugin;

import java.util.List;

public interface StorageAdapter {

    List<StoredData> read(Plugin plugin, String directory);
    List<String> poll(Plugin plugin, String directory);

    void write(Plugin plugin, String directory, List<StoredData> storedDataList);

    void copyDefaults(Plugin plugin, String directory);

    boolean exists(Plugin plugin, String directory);

    String getExtension();

}
