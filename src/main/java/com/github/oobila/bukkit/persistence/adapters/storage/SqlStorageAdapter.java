package com.github.oobila.bukkit.persistence.adapters.storage;

import com.github.oobila.bukkit.persistence.adapters.utils.SqlAdapterUtils;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.util.List;

public class SqlStorageAdapter implements StorageAdapter {

    //

    @Override
    public List<StoredData> read(Plugin plugin, String name) {
        return null;
    }

    @Override
    public List<StoredData> readMetaData(Plugin plugin, String name) {
        return null;
    }

    @Override
    public List<String> poll(Plugin plugin, String name) {
        String query = "SELECT ";

        Connection connection = SqlAdapterUtils.getConnection();
        return null;
    }

    @Override
    public void write(Plugin plugin, String name, List<StoredData> storedDataList) {

    }

    @Override
    public void copyDefaults(Plugin plugin, String name) {

    }

    @Override
    public void delete(Plugin plugin, String name) {

    }

    @Override
    public boolean exists(Plugin plugin, String name) {
        return false;
    }

}
