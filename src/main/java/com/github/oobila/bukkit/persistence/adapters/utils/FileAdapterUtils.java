package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.logging.Level;

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

        return new File(fileLocation, getSimpleFileName(cache));
    }

    public static <V, K> String getSimpleFileName(BaseCache<K,V> cache) {
        String extension = FilenameUtils.getExtension(cache.getName());
        if (extension == null || extension.isEmpty()) {
            return cache.getName() + ".yml";
        } else {
            return cache.getName();
        }
    }

    public static <V extends PersistedObject> V loadConfiguration(File file) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
        return (V) fileConfiguration.get("");
    }

    public static <V extends PersistedObject> V loadConfiguration(InputStream inputStream) {
        FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
        return (V) fileConfiguration.get("");
    }

    public static <V extends PersistedObject> void saveConfiguration(File file, V object) {

    }
}
