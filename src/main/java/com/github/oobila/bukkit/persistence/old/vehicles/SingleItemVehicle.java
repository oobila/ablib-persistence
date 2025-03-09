package com.github.oobila.bukkit.persistence.old.vehicles;

import com.github.oobila.bukkit.persistence.old.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.oobila.bukkit.persistence.old.vehicles.utils.DirectoryUtils.append;
import static com.github.oobila.bukkit.persistence.old.vehicles.utils.BackwardsCompatibilityUtil.compatibility;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Getter
public class SingleItemVehicle<K, V, C extends CacheItem<K, V>> extends BasePersistenceVehicle<K, V, C> {

    private final Class<K> keyType;
    private final StorageAdapter storageAdapter;
    private final CodeAdapter<V> codeAdapter;

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, C> load(Plugin plugin, String directory) {
        codeAdapter.setPlugin(plugin);
        Map<K, C> map = new HashMap<>();
        List<StoredData> storedDataList = storageAdapter.read(plugin, directory);
        storedDataList.forEach(storedData -> {
            K key = Serialization.deserialize(getKeyType(), storedData.getName());
            V value = codeAdapter.toObject(compatibility(this, storedData));
            C cacheItem = (C) new CacheItem<>(
                    this.getCodeAdapter().getType(), key, value, storedData
            );
            map.put(key, cacheItem);
        });
        return map;
    }

    @Override
    public void save(Plugin plugin, String directory, Map<K, C> map) {
        Map.Entry<K, C> entry = map.entrySet().iterator().next();
        String name = Serialization.serialize(entry.getValue().getKey());
        String data = codeAdapter.fromObject(entry.getValue().getData());
        StoredData storedData = new StoredData(directory, data, 0, null);
        storageAdapter.write(plugin, append(directory, name), List.of(storedData));
    }
}
