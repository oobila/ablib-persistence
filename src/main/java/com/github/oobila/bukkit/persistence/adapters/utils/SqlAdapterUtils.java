package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlAdapterUtils {

    private static Set<BaseCache<?,?>> connectionHolders = new HashSet<>();
    @Getter
    private static Connection connection;

    public static Connection createConnection(BaseCache<?,?> cache) {
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
                throw new RuntimeException(e);
            }
        }
        connectionHolders.add(cache);
        return connection;
    }

    public static void closeConnection(BaseCache<?,?> cache) {
        connectionHolders.remove(cache);
        if (connectionHolders.isEmpty()) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String getTableName(BaseCache<?, ?> cache) {
        return StringUtils.replace(cache.getPlugin().getName() + "_" + cache.getName(), "-", "_");
    }

    public static <V> String serializeData(V data) {
        Map<String,Object> map = ((PersistedObject) data).serialize();
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        map.forEach(yamlConfiguration::set);
        String yaml = yamlConfiguration.saveToString();
        return StringUtils.replace(yaml, "'", "''");
    }

    public static <V> V deserializeData(String dataString, Class<V> type) {
        try {
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.loadFromString(dataString);
            Map<String, Object> map = yamlConfiguration.getValues(false);
            return (V) type.getDeclaredMethod("deserialize",  Map.class).invoke(null, map);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
