package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.SqlRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.CacheReader;
import com.github.oobila.bukkit.persistence.adapters.DataCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataSqlAdapter;
import com.github.oobila.bukkit.persistence.adapters.PlayerCacheAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataPlayerFileAdapter;
import com.github.oobila.bukkit.persistence.adapters.DataPlayerSqlAdapter;
import com.github.oobila.bukkit.persistence.adapters.sql.YamlSqlAdapter;
import com.github.oobila.bukkit.persistence.caches.DataCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.google.common.io.CharStreams;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdapterUtils {

    public static <V extends PersistedObject> V deserializeData(
            CacheReader cacheReader, InputStream inputStream, Class<V> type
    ) {
        return deserializeData(cacheReader, new InputStreamReader(inputStream), type);
    }

    public static <V extends PersistedObject> V deserializeData(
            CacheReader cacheReader, Reader reader, Class<V> type
    ) {
        try {
            YamlConfiguration yamlConfiguration = loadYaml(cacheReader, CharStreams.toString(reader));
            return AdapterUtils.deserializeData(yamlConfiguration, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <V extends PersistedObject> V deserializeData(
            CacheReader cacheReader, String dataString, Class<V> type
    ) {
        YamlConfiguration yamlConfiguration = loadYaml(cacheReader, dataString);
        return deserializeData(yamlConfiguration, type);
    }

    public static <V extends PersistedObject> V deserializeData(
            YamlConfiguration yamlConfiguration, Class<V> type
    ) {
        try {
            Map<String, Object> map = yamlConfiguration.getValues(false);
            return (V) type.getDeclaredMethod("deserialize",  Map.class).invoke(null, map);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new SqlRuntimeException(e);
        }
    }


    public static YamlConfiguration loadYaml(CacheReader cacheReader, Reader reader) {
        try {
            return loadYaml(cacheReader, CharStreams.toString(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static YamlConfiguration loadYaml(CacheReader cacheReader, String string) {
        try {
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            for (Map.Entry<String, String> rule : cacheReader.getDeserializeReplacementRules().entrySet()) {
                string = string.replace(rule.getKey(), rule.getValue());
            }
            yamlConfiguration.loadFromString(string);
            return yamlConfiguration;
        } catch (InvalidConfigurationException e) {
            log(Level.SEVERE, "Could not load YAML from string: {0}", string);
            Bukkit.shutdown();
            return null;
        }
    }

}
