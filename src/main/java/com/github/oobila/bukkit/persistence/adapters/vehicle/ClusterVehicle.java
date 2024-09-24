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
            List<StoredData> storedDataList = storageAdapter.read(plugin, String.format("%s/%s", directory, item));
            storedDataList.forEach(storedData -> {
                K key = Serialization.deserialize(getKeyType(), storedData.getName());
                V value = codeAdapter.toObject(compatibility(this, storedData));
                CacheItem<K, V> cacheItem = new CacheItem<>(key, value, storedData);
                map.put(key, cacheItem);
            });
        }
        return map;
    }

    @Override
    public void save(Plugin plugin, String directory, Map<K, CacheItem<K,V>> map) {
        map.forEach((key, value) -> saveSingle(plugin, directory, value));
    }

    @Override
    public void saveSingle(Plugin plugin, String directory, CacheItem<K, V> cacheItem) {
        String name = Serialization.serialize(cacheItem.getKey());
        String data = codeAdapter.fromObject(cacheItem.getData());
        StoredData storedData = new StoredData(name, data, 0, null);
        storageAdapter.write(plugin, String.format("%s/%s", directory, name), List.of(storedData));
    }
}
