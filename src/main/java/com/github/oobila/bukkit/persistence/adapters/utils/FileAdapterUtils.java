package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.adapters.CacheReader;
import com.github.oobila.bukkit.persistence.adapters.DataCacheAdapter;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import com.google.common.io.CharStreams;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileAdapterUtils {

    public static <V, K> void copyDefaults(BaseCache<K,V> cache, File file){
        file.getParentFile().mkdirs();
        try (InputStream inputStream = cache.getPlugin().getResource(getSimpleFileName(cache));
             OutputStream outputStream = new FileOutputStream(file)) {
            if (inputStream != null) {
                Bukkit.getLogger().log(Level.INFO, "Copying defaults for: " + cache.getName());
                byte[] buffer = new byte[1024];
                int len;
                while((len = inputStream.read(buffer))>0) {
                    outputStream.write(buffer,0,len);
                }
            }
            outputStream.flush();
        } catch (IOException e) {
            //silent catch in case no default file exists
        }
    }

    public static <V, K> File getSaveFile(BaseCache<K,V> cache, OfflinePlayer player) {
        return new File(getClusterLocation(cache, player), getSimpleFileName(cache));
    }

    public static <V, K> File getClusterLocation(BaseCache<K,V> cache, OfflinePlayer player) {
        File fileLocation = cache.getPlugin().getDataFolder();

        //append data prefix
        if (cache.getSubFolderName() != null) {
            fileLocation = new File(fileLocation, cache.getSubFolderName());
            try {
                FileUtils.forceMkdir(fileLocation);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //append player prefix
            if (player != null) {
                fileLocation = new File(fileLocation, Serialization.serialize(player));
                try {
                    FileUtils.forceMkdir(fileLocation);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return fileLocation;
    }

    public static <V, K> String getSimpleFileName(BaseCache<K,V> cache) {
        return getSimpleFileName(cache.getName());
    }

    public static String getSimpleFileName(String input) {
        String extension = FilenameUtils.getExtension(input);
        if (extension == null || extension.isEmpty()) {
            return input + ".yml";
        } else {
            return input;
        }
    }

    public static YamlConfiguration loadYaml(CacheReader cacheReader, File file) {
        try {
            return loadYaml(cacheReader, new FileReader(file), file.getName());
        } catch (FileNotFoundException e) {
            log(Level.WARNING, "Could not find file - {0}", file.getName());
            return new YamlConfiguration();
        }
    }

    public static YamlConfiguration loadYaml(CacheReader cacheReader, Reader reader, String name) {
        try {
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            String yamlString = CharStreams.toString(reader);
            for (Map.Entry<String, String> rule : cacheReader.getDeserializeReplacementRules().entrySet()) {
                yamlString = yamlString.replace(rule.getKey(), rule.getValue());
            }
            yamlConfiguration.loadFromString(yamlString);
            return yamlConfiguration;
        } catch (InvalidConfigurationException | IOException e) {
            log(Level.SEVERE, "Could not load YAML from reader - {0}", name);
            Bukkit.shutdown();
            return null;
        }
    }

    public static <V extends PersistedObject> V loadConfiguration(DataCacheAdapter<?,?> adapter, File file) {
        YamlConfiguration yamlConfiguration = loadYaml(adapter, file);
        return (V) yamlConfiguration.get("data");
    }

    public static <V extends PersistedObject> V loadConfiguration(DataCacheAdapter<?,?> adapter, InputStream inputStream) {
        YamlConfiguration yamlConfiguration = loadYaml(adapter, new InputStreamReader(inputStream), "inputStream");
        return (V) yamlConfiguration.get("data");
    }

    public static <V extends PersistedObject> void saveConfiguration(File file, V object) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.set("data", object);
        try (Writer writer = new FileWriter(file)) {
            String string = yamlConfiguration.saveToString();
            writer.write(string);
        } catch (IOException e) {
            log(Level.WARNING, "Could not save YAML for - {0}", file.getName());
        }
    }
}
