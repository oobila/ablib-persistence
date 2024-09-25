package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.caches.standard.NoMemoryClusterCache;
import com.github.oobila.bukkit.persistence.model.NoMemoryCacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class NoMemoryClusterVehicle<K, V> extends ClusterVehicle<K, V> {

    public NoMemoryClusterVehicle(Class<K> keyType, StorageAdapter storageAdapter, CodeAdapter<V> codeAdapter) {
        super(keyType, storageAdapter, codeAdapter);
    }

    @SuppressWarnings("unchecked")
    public NoMemoryCacheItem<K,V> loadMetadataSingle(Plugin plugin, String directory) {
        Map<K, NoMemoryCacheItem<K,V>> map = new HashMap<>();
        List<StoredData> storedDataList = getStorageAdapter().read(plugin, directory);
        K key = Serialization.deserialize(getKeyType(), storedDataList.get(0).getName());
        return new NoMemoryCacheItem<>(key, null, storedDataList.get(0), (NoMemoryClusterCache<K, V>) getCache());
    }

}
