package com.github.oobila.bukkit.persistence.adapters;

import com.github.oobila.bukkit.persistence.adapters.utils.FileAdapterUtils;
import com.github.oobila.bukkit.persistence.caches.BaseCache;
import com.github.oobila.bukkit.persistence.model.PersistedObject;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataClusterFileAdapter<K, V extends PersistedObject> extends DataFileAdapter<K, V> {

    private final Map<K, V> localCache = new HashMap<>();

    @Override
    public void open(BaseCache<K, V> cache) {
        File clusterLocation = FileAdapterUtils.getClusterLocation(cache, null);
        File[] files = clusterLocation.listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(file -> {
                String fileName = FilenameUtils.removeExtension(file.getName());
                K key = Serialization.deserialize(cache.getKeyType(), fileName);
                localCache.put(key, FileAdapterUtils.loadConfiguration(this, file));
            });
        }
    }

    @Override
    public void close(BaseCache<K, V> cache) {
        File clusterLocation = FileAdapterUtils.getClusterLocation(cache, null);
        if (clusterLocation.exists()) {
            clusterLocation.delete();
        }
        localCache.forEach((k, v) -> {
            File saveFile = new File(clusterLocation, Serialization.serialize(k) + ".yml");
            FileAdapterUtils.saveConfiguration(saveFile, v);
        });
    }

    protected V onLoad(File saveFile) {
        return FileAdapterUtils.loadConfiguration(this, saveFile);
    }

    protected void onSave(File saveFile, V value) {
        FileAdapterUtils.saveConfiguration(saveFile, value);
    }

}
