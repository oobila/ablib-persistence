package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.adapters.code.ResourcePackCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.adapters.storage.ZipStorageAdapter;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.model.Resource;
import com.github.oobila.bukkit.persistence.model.ResourcePack;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.Plugin;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.oobila.bukkit.persistence.utils.BackwardsCompatibilityUtil.compatibility;

@RequiredArgsConstructor
@Getter
public class ResourcePackVehicle<K> extends BasePersistenceVehicle<K, ResourcePack> {

    private static final ZonedDateTime OLD_DATE = ZonedDateTime.of(
            2000,1,1,0,0,0,0, ZoneOffset.UTC);

    private final Class<K> keyType;
    private final StorageAdapter storageAdapter = new ZipStorageAdapter();
    private final ResourcePackCodeAdapter codeAdapter;

    @Override
    public Map<K, CacheItem<K, ResourcePack>> load(Plugin plugin, String directory) {
        Map<K, CacheItem<K,ResourcePack>> map = new HashMap<>();
        for (String item : storageAdapter.poll(plugin, directory)) {
            long size = 0;
            ZonedDateTime updatedDate = OLD_DATE;
            List<StoredData> storedDataList = storageAdapter.read(plugin, String.format("%s/%s", directory, item));
            ResourcePack resourcePack = new ResourcePack(FilenameUtils.getBaseName(item));
            for (StoredData storedData : storedDataList) {
                size += storedData.getSize();
                if (updatedDate.isBefore(storedData.getUpdatedDate())) {
                    updatedDate = storedData.getUpdatedDate();
                }
                Object object = codeAdapter.toObject(compatibility(this, storedData));
                Resource<?> resource = new Resource<>(
                        storedData.getName(),
                        object,
                        storedData.getSize(),
                        storedData.getUpdatedDate()
                );
                resourcePack.put(resource.getKey(), resource);
            }
            K key = Serialization.deserialize(getKeyType(), FilenameUtils.getBaseName(item));
            StoredData storedData = new StoredData(null, null, size, updatedDate);
            CacheItem<K, ResourcePack> cacheItem = new CacheItem<>(key, resourcePack, storedData);
            map.put(key, cacheItem);
        }
        return map;
    }

    @Override
    public void save(Plugin plugin, String directory, Map<K, CacheItem<K, ResourcePack>> map) {
        map.forEach((key, value) ->
                saveSingle(plugin, directory, value)
        );
    }

    @Override
    public void saveSingle(Plugin plugin, String directory, CacheItem<K, ResourcePack> cacheItem) {
        String name = Serialization.serialize(cacheItem.getKey());
        List<StoredData> storedDataList = new ArrayList<>();
        for (Map.Entry<String, Resource<?>> entry : cacheItem.getData().entrySet()) {
            String data = codeAdapter.fromObject(entry.getValue());
            storedDataList.add(new StoredData(name, data, 0, null));
        }
        storageAdapter.write(plugin, String.format("%s/%s", directory, name), storedDataList);
    }
}
