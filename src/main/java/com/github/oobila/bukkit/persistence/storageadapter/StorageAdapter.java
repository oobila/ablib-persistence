package com.github.oobila.bukkit.persistence.storageadapter;

import com.github.oobila.bukkit.persistence.codeadapter.model.StoredData;
import org.bukkit.plugin.Plugin;

import java.util.List;

public interface StorageAdapter {

    void write(String path, StoredData data);

    List<String> poll(String path);

    StoredData read(String path);

    StoredData readMetadata(String path);

    void delete(String path);

    void register(Plugin plugin);

}
