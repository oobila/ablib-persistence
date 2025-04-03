package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.caches.SqlCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlAdapterUtils {

    private static final Set<SqlCache> connectionHolders = new HashSet<>();
    @Getter
    private static Connection connection;

    @SuppressWarnings("java:S4925") //this is required to load the correct SQL driver
    public static Connection createConnection(SqlCache cache) {
        if(connectionHolders.isEmpty()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + cache.getSqlConnectionProperties().getHostname() + ":" +
                                cache.getSqlConnectionProperties().getPort() + "/" +
                                cache.getSqlConnectionProperties().getDatabase(),
                        cache.getSqlConnectionProperties().getUsername(),
                        cache.getSqlConnectionProperties().getPassword()
                );
            } catch (SQLException | ClassNotFoundException e) {
                throw new SqlRuntimeException(e);
            }
        }
        connectionHolders.add(cache);
        return connection;
    }

    public static void closeConnection(SqlCache cache) {
        connectionHolders.remove(cache);
        if (connectionHolders.isEmpty()) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                throw new SqlRuntimeException(e);
            }
        }
    }

    public static String getTableName(SqlCache cache) {
//        return StringUtils.replace(cache.getPlugin().getName() + "_" + cache.getName(), "-", "_");
        return "";
    }

    public static <V extends ConfigurationSerializable> String serializeData(V data) {
        Map<String,Object> map = data.serialize();
        MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
        map.forEach(yamlConfiguration::set);
        String yaml = yamlConfiguration.saveToString();
        return StringUtils.replace(yaml, "'", "''");
    }

    @SuppressWarnings("unchecked")
    public static <V extends ConfigurationSerializable> V deserializeData(Class<V> vClass, String data) {
        try {
            MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
            yamlConfiguration.loadFromString(data);
            Map<String, Object> map = yamlConfiguration.getValues(false);
            return (V) vClass.getDeclaredMethod("deserialize", Map.class).invoke(null, map);
        } catch (InvalidConfigurationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new SqlRuntimeException(e);
        }
    }

}