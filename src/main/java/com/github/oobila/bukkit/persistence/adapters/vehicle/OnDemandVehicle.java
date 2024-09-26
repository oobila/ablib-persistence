package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.caches.standard.OnDemandCache;
import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.oobila.bukkit.persistence.adapters.utils.DirectoryUtils.append;

@SuppressWarnings("unused")
public class OnDemandVehicle<K, V, C extends OnDemandCacheItem<K, V>>
        extends ClusterVehicle<K, V, C> implements OnDemandPersistenceVehicle<K, V, C> {

    public OnDemandVehicle(Class<K> keyType, StorageAdapter storageAdapter, CodeAdapter<V> codeAdapter) {
        super(keyType, storageAdapter, codeAdapter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public C loadMetadataSingle(Plugin plugin, String directory, String name) {
        getCodeAdapter().setPlugin(plugin);
        Map<K, OnDemandCacheItem<K,V>> map = new HashMap<>();
        List<StoredData> storedDataList = getStorageAdapter().read(plugin, append(directory, name));
        K key = Serialization.deserialize(getKeyType(), storedDataList.get(0).getName());
        return (C) new OnDemandCacheItem<>(key, null, storedDataList.get(0), (OnDemandCache<K, V>) getCache());
    }

}
