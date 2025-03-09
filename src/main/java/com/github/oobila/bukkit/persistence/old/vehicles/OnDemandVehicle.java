package com.github.oobila.bukkit.persistence.old.vehicles;

import com.github.oobila.bukkit.persistence.old.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.caches.standard.OnDemandCache;
import com.github.oobila.bukkit.persistence.old.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import com.github.oobila.bukkit.persistence.old.vehicles.global.ClusterVehicle;
import com.github.oobila.bukkit.persistence.old.vehicles.global.OnDemandPersistenceVehicle;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static com.github.oobila.bukkit.persistence.old.vehicles.utils.DirectoryUtils.append;

@SuppressWarnings("unused")
public class OnDemandVehicle<K, V, C extends OnDemandCacheItem<K, V>>
        extends ClusterVehicle<K, V, C> implements OnDemandPersistenceVehicle<K, V, C> {

    public OnDemandVehicle(Class<K> keyType, StorageAdapter storageAdapter, CodeAdapter<V> codeAdapter) {
        super(keyType, storageAdapter, codeAdapter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public C loadMetadataSingle(Plugin plugin, String directory, String name) {
        this.getCodeAdapter().setPlugin(plugin);
        List<StoredData> storedDataList = getStorageAdapter().read(plugin, append(directory, name));
        K key = Serialization.deserialize(getKeyType(), storedDataList.get(0).getName());
        return (C) new OnDemandCacheItem<>(
                this.getCodeAdapter().getType(), key, null, storedDataList.get(0), (OnDemandCache<K, V>) getCache()
        );
    }

}
