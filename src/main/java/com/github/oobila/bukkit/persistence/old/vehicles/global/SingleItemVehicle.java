package com.github.oobila.bukkit.persistence.old.vehicles.global;

import com.github.oobila.bukkit.persistence.old.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import com.github.oobila.bukkit.persistence.old.vehicles.utils.CacheItemFactory;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.oobila.bukkit.persistence.old.vehicles.utils.BackwardsCompatibilityUtil.compatibility;
import static com.github.oobila.bukkit.persistence.old.vehicles.utils.DirectoryUtils.append;

@Getter
public class SingleItemVehicle<K, V, C extends CacheItem<K, V>>
        extends BaseGlobalPersistenceVehicle<K, V, C> implements GlobalPersistenceVehicle<K, V, C> {

    private final StorageAdapter storageAdapter;
    private final CodeAdapter<V> codeAdapter;
    private final CacheItemFactory<K, V> vehicleLoader;

    public SingleItemVehicle(Class<K> keyType, StorageAdapter storageAdapter, CodeAdapter<V> codeAdapter, CacheItemFactory<K, V> vehicleLoader) {
        super(keyType);
        this.storageAdapter = storageAdapter;
        this.codeAdapter = codeAdapter;
        this.vehicleLoader = vehicleLoader;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, C> load() {
        codeAdapter.setPlugin(getPlugin());
        Map<K, C> map = new HashMap<>();
        List<StoredData> storedDataList = storageAdapter.read(getPlugin(), getCache().getName());
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
    public void save(Map<K, C> map) {
        Map.Entry<K, C> entry = map.entrySet().iterator().next();
        String name = Serialization.serialize(entry.getValue().getKey());
        String data = codeAdapter.fromObject(entry.getValue().getData());
        StoredData storedData = new StoredData(getCache().getName(), data, 0, null);
        storageAdapter.write(getPlugin(), append(getCache().getName(), name), List.of(storedData));
    }
}
