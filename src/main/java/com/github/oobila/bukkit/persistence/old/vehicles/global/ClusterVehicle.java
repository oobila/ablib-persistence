package com.github.oobila.bukkit.persistence.old.vehicles.global;

import com.github.oobila.bukkit.persistence.old.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.caches.Cache;
import com.github.oobila.bukkit.persistence.old.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.oobila.bukkit.persistence.old.vehicles.utils.BackwardsCompatibilityUtil.compatibility;
import static com.github.oobila.bukkit.persistence.old.vehicles.utils.DirectoryUtils.append;

@SuppressWarnings("unused")
@Getter
public class ClusterVehicle<K, V, C extends OnDemandCacheItem<K, V>>
        extends BaseGlobalPersistenceVehicle<K, V, C> implements OnDemandPersistenceVehicle<K, V, C> {

    private final StorageAdapter storageAdapter;
    private final CodeAdapter<V> codeAdapter;

    public ClusterVehicle(Class<K> keyType, StorageAdapter storageAdapter, CodeAdapter<V> codeAdapter) {
        super(keyType);
        this.storageAdapter = storageAdapter;
        this.codeAdapter = codeAdapter;
    }



    @Override
    public Map<K, C> loadMetadata() {
        codeAdapter.setPlugin(getPlugin());
        Map<K, C> map = new HashMap<>();
        for (String item : storageAdapter.poll(getPlugin(), getCache().getName())) {
            C cacheItem = loadMetadataSingle(item);
            map.put(cacheItem.getKey(), cacheItem);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public C loadSingle(String name) {
        Map<K, C> map = new HashMap<>();
        List<StoredData> storedDataList = storageAdapter.read(getPlugin(), append(getCache().getName(), name));
        K key = Serialization.deserialize(getKeyType(), storedDataList.get(0).getName());
        V value = codeAdapter.toObject(compatibility(this, storedDataList.get(0)));
        return (C) new OnDemandCacheItem<>(
                this.getCodeAdapter().getType(), key, value, storedDataList.get(0), (Cache<K, V, OnDemandCacheItem<K, V>>) getCache()
        );
    }

    @Override
    public void save(Map<K, C> map) {
        map.forEach((key, value) -> {
            deleteSingle(key);
            saveSingle(value);
        });
    }

    @Override
    public void deleteSingle(K key) {
        String name = Serialization.serialize(key);
        storageAdapter.delete(plugin, append(directory, name));
    }

    @Override
    public C loadMetadataSingle(String name) {
        return null;
    }
}
