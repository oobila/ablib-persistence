package com.github.oobila.bukkit.persistence.old.vehicles;

import com.github.oobila.bukkit.persistence.old.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.caches.standard.OnDemandCache;
import com.github.oobila.bukkit.persistence.old.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import com.github.oobila.bukkit.persistence.old.vehicles.player.OnDemandPersistenceVehicle;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

import static com.github.oobila.bukkit.persistence.old.vehicles.utils.DirectoryUtils.playerDir;

public class OnDemandPlayerVehicle<K, V, C extends OnDemandCacheItem<K, V>> extends PlayerClusterVehicle<K, V, C>
        implements OnDemandPersistenceVehicle<K, V, C> {

    public OnDemandPlayerVehicle(Class<K> keyType, StorageAdapter storageAdapter, CodeAdapter<V> codeAdapter) {
        super(keyType, storageAdapter, codeAdapter);
    }

    @SuppressWarnings("unchecked")
    @Override
    public C loadMetadataSingle(Plugin plugin, String directory, UUID playerId, String name) {
        this.getCodeAdapter().setPlugin(plugin);
        List<StoredData> storedDataList = getStorageAdapter().read(plugin, playerDir(directory, playerId, name));
        StoredData storedData = storedDataList.get(0).toBuilder().ownerId(playerId).build();
        K key = Serialization.deserialize(getKeyType(), storedData.getName());
        return (C) new OnDemandCacheItem<>(
                this.getCodeAdapter().getType(), key, null, storedData, (OnDemandCache<K, V>) getCache()
        );
    }
}
