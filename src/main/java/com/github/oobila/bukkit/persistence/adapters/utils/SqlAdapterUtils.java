package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.SqlRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.CacheReader;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SqlAdapterUtils {

    private static Set<BaseCache<?,?>> connectionHolders = new HashSet<>();
    @Getter
    private static Connection connection;

    @SuppressWarnings("java:S4925") //this is required to load the correct SQL driver
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
                throw new SqlRuntimeException(e);
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
                throw new SqlRuntimeException(e);
            }
        }
    }

    public static String getTableName(BaseCache<?, ?> cache) {
        return StringUtils.replace(cache.getPlugin().getName() + "_" + cache.getName(), "-", "_");
    }

    public static <V extends PersistedObject> String serializeData(V data) {
        Map<String,Object> map = data.serialize();
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        map.forEach(yamlConfiguration::set);
        String yaml = yamlConfiguration.saveToString();
        return StringUtils.replace(yaml, "'", "''");
    }

    public static <V extends PersistedObject> V deserializeData(CacheReader cacheReader, String dataString, Class<V> type) {
        try {
            YamlConfiguration yamlConfiguration = loadYaml(cacheReader, dataString, type.getName());
            Map<String, Object> map = yamlConfiguration.getValues(false);
            return (V) type.getDeclaredMethod("deserialize",  Map.class).invoke(null, map);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new SqlRuntimeException(e);
        }
    }

    public static YamlConfiguration loadYaml(CacheReader cacheReader, String string, String name) {
        try {
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            for (Map.Entry<String, String> rule : cacheReader.getDeserializeReplacementRules().entrySet()) {
                string = string.replace(rule.getKey(), rule.getValue());
            }
            yamlConfiguration.loadFromString(string);
            return yamlConfiguration;
        } catch (InvalidConfigurationException e) {
            log(Level.SEVERE, "Could not load YAML from SQL - {0}", name);
            Bukkit.shutdown();
            return null;
        }
    }

}
