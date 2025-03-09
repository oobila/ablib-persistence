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
public class ClusterVehicle<K, V, C extends CacheItem<K, V>>
        extends BasePersistenceVehicle<K, V, C> implements ClusterPersistenceVehicle<K, V, C> {

    private final Class<K> keyType;
    private final StorageAdapter storageAdapter;
    private final CodeAdapter<V> codeAdapter;

    @Override
    public Map<K, C> load(Plugin plugin, String directory) {
        codeAdapter.setPlugin(plugin);
        Map<K, C> map = new HashMap<>();
        for (String item : storageAdapter.poll(plugin, directory)) {
            C cacheItem = loadSingle(plugin, directory, item);
            map.put(cacheItem.getKey(), cacheItem);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public C loadSingle(Plugin plugin, String directory, String name) {
        codeAdapter.setPlugin(plugin);
        Map<K, C> map = new HashMap<>();
        List<StoredData> storedDataList = storageAdapter.read(plugin, append(directory, name));
        K key = Serialization.deserialize(getKeyType(), storedDataList.get(0).getName());
        V value = codeAdapter.toObject(compatibility(this, storedDataList.get(0)));
        return (C) new CacheItem<>(
                this.getCodeAdapter().getType(), key, value, storedDataList.get(0)
        );
    }

    @Override
    public void save(Plugin plugin, String directory, Map<K, C> map) {
        map.forEach((key, value) -> {
            deleteSingle(plugin, directory, key);
            saveSingle(plugin, directory, value);
        });
    }

    @Override
    public void saveSingle(Plugin plugin, String directory, C cacheItem) {
        String name = Serialization.serialize(cacheItem.getKey());
        String data = codeAdapter.fromObject(cacheItem.getData());
        StoredData storedData = new StoredData(name, data, 0, null);
        storageAdapter.write(plugin, append(directory, name), List.of(storedData));
    }

    @Override
    public void deleteSingle(Plugin plugin, String directory, K key) {
        String name = Serialization.serialize(key);
        storageAdapter.delete(plugin, append(directory, name));
    }
}
