package com.github.oobila.bukkit.persistence.adapters.storage;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.utils.SqlAdapterUtils;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SqlStorageAdapter implements StorageAdapter {

    private static final String TABLE_NAME = "table";
    public static final String PARTITION_NAME = "partition_id";
    public static final String KEY_NAME = "record_key";
    public static final String DATA_NAME = "data";
    public static final String DATE_NAME = "created";
    public static final String KEY_VALUE_SEPARATOR = "=";
    public static final String SEPARATOR = ";";
    public static final String NULL_STRING = "NULL";

    public SqlStorageAdapter(String pluginName, String tableName, SqlConnectionProperties connectionProperties) {
        String finalName = toTableName(pluginName, tableName);
        SqlAdapterUtils.createConnection(finalName, connectionProperties);
        createTable(finalName);
    }

    @Override
    public List<StoredData> read(Plugin plugin, String name) {
        NameParts nameParts = split(plugin, name);
        String query = String.format("SELECT * FROM %s%s;", nameParts.tableName, constructWhere(nameParts));
        Connection connection = SqlAdapterUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            List<StoredData> retList = new ArrayList<>();
            while (rs.next()) {
                String data = rs.getString(DATA_NAME);
                StoredData storedData = new StoredData(
                        rs.getString(KEY_NAME),
                        data,
                        data.length(),
                        rs.getTimestamp(DATE_NAME).toLocalDateTime().atZone(ZoneId.systemDefault())
                );
                retList.add(storedData);
            }
            return retList;
        } catch (SQLException e) {
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public List<StoredData> readMetaData(Plugin plugin, String name) {
        NameParts nameParts = split(plugin, name);
        String query = String.format("SELECT partition_id, record_key, created FROM %s%s;", nameParts.tableName, constructWhere(nameParts));
        Connection connection = SqlAdapterUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            List<StoredData> retList = new ArrayList<>();
            while (rs.next()) {
                String data = rs.getString(DATA_NAME);
                StoredData storedData = new StoredData(
                        rs.getString(KEY_NAME),
                        null,
                        data.length(),
                        rs.getTimestamp(DATE_NAME).toLocalDateTime().atZone(ZoneId.systemDefault())
                );
                retList.add(storedData);
            }
            return retList;
        } catch (SQLException e) {
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public List<String> poll(Plugin plugin, String name) {
        NameParts nameParts = split(plugin, name);
        String query = String.format("SELECT record_key FROM %s%s;", nameParts.tableName, constructWhere(nameParts));
        Connection connection = SqlAdapterUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            List<String> retList = new ArrayList<>();
            while (rs.next()) {
                retList.add(rs.getString(KEY_NAME));
            }
            return retList;
        } catch (SQLException e) {
            throw new PersistenceRuntimeException(e);
        }
    }

    private String wrap(String string) {
        if (string == null) {
            return NULL_STRING;
        }
        return String.format("'%s'", string);
    }

    @Override
    public void write(Plugin plugin, String name, List<StoredData> storedDataList) {
        NameParts nameParts = split(plugin, name);
        for (StoredData storedData : storedDataList) {
            String query = String.format(
                    "INSERT INTO %s (partition_id, record_key, data, created) VALUES (%s, %s, %s, NOW());",
                    nameParts.tableName,
                    wrap(nameParts.partition),
                    wrap(storedData.getName()),
                    wrap(StringUtils.replace(storedData.getData(), "'", "''"))
            );
            Connection connection = SqlAdapterUtils.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new PersistenceRuntimeException(e);
            }
        }
    }

    @Override
    public void copyDefaults(Plugin plugin, String name) {
        throw new PersistenceRuntimeException("Cannot copy defaults to SQL");
    }

    @Override
    public void delete(Plugin plugin, String name) {
        NameParts nameParts = split(plugin, name);
        String query = String.format("DELETE FROM %s%s;", nameParts.tableName, constructWhere(nameParts));
        Connection connection = SqlAdapterUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public boolean exists(Plugin plugin, String name) {
        NameParts nameParts = split(plugin, name);
        return poll(plugin, name).contains(nameParts.key);
    }

    @Override
    public ZonedDateTime getLastUpdated(Plugin plugin, String name) {
        NameParts nameParts = split(plugin, name);
        String query = String.format("SELECT created FROM %s ORDER BY created DESC limit 1;", nameParts.tableName);
        Connection connection = SqlAdapterUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp(DATE_NAME).toLocalDateTime().atZone(ZoneId.systemDefault());
            }
        } catch (SQLException e) {
            throw new PersistenceRuntimeException(e);
        }
        return null;
    }

    private void createTable(String tableName) {
        String query = String.format(
                "CREATE TABLE IF NOT EXISTS %s (partition_id TINYTEXT, record_key TINYTEXT NOT NULL, data LONGTEXT, created TIMESTAMP);",
                tableName
        );
        Connection connection = SqlAdapterUtils.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenceRuntimeException(e);
        }
    }

    private NameParts split(Plugin plugin, String s) {
        return split(plugin.getName(), s);
    }

    private NameParts split(String pluginName, String s) {
        NameParts nameParts = new NameParts();
        String[] strings = s.split("[;,]");
        for (String string : strings) {
            String[] keyValue = string.split("[=:]");
            switch (keyValue[0]) {
                case TABLE_NAME -> nameParts.tableName = toTableName(pluginName, keyValue[1]);
                case PARTITION_NAME -> nameParts.partition = keyValue[1].equals(DynamicVehicle.PARTITION_STRING) ? null : keyValue[1];
                case KEY_NAME -> nameParts.key = keyValue[1].equals(DynamicVehicle.KEY_STRING) ? null : keyValue[1];
                default -> throw new PersistenceRuntimeException(String.format("unknown key type: %s", keyValue[0]));
            }
        }
        return nameParts;
    }

    private String toTableName(String pluginName, String tableName) {
        return String.format("%s__%s", pluginName, tableName).toLowerCase();
    }

    private String constructWhere(NameParts nameParts) {
        if (nameParts.partition == null && nameParts.key == null) {
            return Strings.EMPTY;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(" WHERE ");
            List<String> parts = new ArrayList<>();
            if (nameParts.partition != null) {
                parts.add(String.format("partition_id = %s", wrap(nameParts.partition)));
            }
            if (nameParts.key != null) {
                parts.add(String.format("record_key = %s", wrap(nameParts.key)));
            }
            String combined = String.join(" AND ", parts);
            sb.append(combined);
            return sb.toString();
        }
    }

    private class NameParts {
        public String tableName;
        public String partition;
        public String key;
    }

}
