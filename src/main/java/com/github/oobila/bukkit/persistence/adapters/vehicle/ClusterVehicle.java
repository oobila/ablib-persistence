package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.oobila.bukkit.persistence.adapters.utils.DirectoryUtils.append;
import static com.github.oobila.bukkit.persistence.utils.BackwardsCompatibilityUtil.compatibility;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Getter
public class ClusterVehicle<K, V> extends BasePersistenceVehicle<K, V> {

    private final Class<K> keyType;
    private final StorageAdapter storageAdapter;
    private final CodeAdapter<V> codeAdapter;

    @Override
    public Map<K, CacheItem<K,V>> load(Plugin plugin, String directory) {
        Map<K, CacheItem<K,V>> map = new HashMap<>();
        for (String item : storageAdapter.poll(plugin, directory)) {
            CacheItem<K, V> cacheItem = loadSingle(plugin, append(directory, item));
            map.put(cacheItem.getKey(), cacheItem);
        }
        return map;
    }

    public CacheItem<K,V> loadSingle(Plugin plugin, String directory) {
        Map<K, CacheItem<K,V>> map = new HashMap<>();
        List<StoredData> storedDataList = storageAdapter.read(plugin, directory);
        K key = Serialization.deserialize(getKeyType(), storedDataList.get(0).getName());
        V value = codeAdapter.toObject(compatibility(this, storedDataList.get(0)));
        return new CacheItem<>(key, value, storedDataList.get(0));
    }

    @Override
    public void save(Plugin plugin, String directory, Map<K, CacheItem<K,V>> map) {
        map.forEach((key, value) -> {
            String item = Serialization.serialize(key);
            deleteSingle(plugin, append(directory, item));
            saveSingle(plugin, directory, value);
        });
    }

    @Override
    public void saveSingle(Plugin plugin, String directory, CacheItem<K, V> cacheItem) {
        String name = Serialization.serialize(cacheItem.getKey());
        String data = codeAdapter.fromObject(cacheItem.getData());
        StoredData storedData = new StoredData(name, data, 0, null);
        storageAdapter.write(plugin, append(directory, name), List.of(storedData));
    }

    public void deleteSingle(Plugin plugin, String directory) {
        storageAdapter.delete(plugin, directory);
    }
}
