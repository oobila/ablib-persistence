//package com.github.oobila.bukkit.persistence.adapters.vehicle;
//
//import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
//import com.github.oobila.bukkit.persistence.adapters.code.DummyCodeAdapter;
//import com.github.oobila.bukkit.persistence.adapters.code.ResourcePackCodeAdapter;
//import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
//import com.github.oobila.bukkit.persistence.adapters.storage.ZipStorageAdapter;
//import com.github.oobila.bukkit.persistence.model.CacheItem;
//import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;
//import com.github.oobila.bukkit.persistence.model.Resource;
//import com.github.oobila.bukkit.persistence.model.ResourcePack;
//import com.github.oobila.bukkit.persistence.serializers.Serialization;
//import lombok.Getter;
//import org.apache.commons.io.FilenameUtils;
//import org.bukkit.plugin.Plugin;
//
//import java.time.ZoneOffset;
//import java.time.ZonedDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Pattern;
//
//import static com.github.oobila.bukkit.persistence.adapters.utils.DirectoryUtils.append;
//import static com.github.oobila.bukkit.persistence.utils.BackwardsCompatibilityUtil.compatibility;
//
//@Getter
//public class ResourcePackVehicle<K>
//        extends BasePersistenceVehicle<K, ResourcePack, OnDemandCacheItem<K, ResourcePack>>
//        implements OnDemandPersistenceVehicle<K, ResourcePack, OnDemandCacheItem<K, ResourcePack>> {
//
//    private static final ZonedDateTime OLD_DATE = ZonedDateTime.of(
//            2000,1,1,0,0,0,0, ZoneOffset.UTC);
//
//    private final Class<K> keyType;
//    private final ResourcePackCodeAdapter resourceCodeAdapter;
//    private final DummyCodeAdapter<ResourcePack> codeAdapter = new DummyCodeAdapter<>(ResourcePack.class);
//    private final ZipStorageAdapter storageAdapter = new ZipStorageAdapter();
//
//    public ResourcePackVehicle(Class<K> keyType, Map<Pattern, CodeAdapter<?>> codeAdapterMap) {
//        this.keyType = keyType;
//        this.resourceCodeAdapter = new ResourcePackCodeAdapter(codeAdapterMap);
//    }
//
//    @Override
//    public Map<K, OnDemandCacheItem<K, ResourcePack>> load(Plugin plugin, String directory) {
//        resourceCodeAdapter.setPlugin(plugin);
//        Map<K, OnDemandCacheItem<K,ResourcePack>> map = new HashMap<>();
//        for (String item : storageAdapter.poll(plugin, directory)) {
//            K key = Serialization.deserialize(getKeyType(), FilenameUtils.getBaseName(item));
//            map.put(key, loadSingle(plugin, directory, item));
//        }
//        return map;
//    }
//
//    @Override
//    public void save(Plugin plugin, String directory, Map<K, OnDemandCacheItem<K, ResourcePack>> map) {
//        map.forEach((key, value) ->
//                saveSingle(plugin, directory, value)
//        );
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public OnDemandCacheItem<K, ResourcePack> loadSingle(Plugin plugin, String directory, String name) {
//        long size = 0;
//        ZonedDateTime updatedDate = OLD_DATE;
//        List<StoredData> storedDataList = storageAdapter.read(plugin, append(directory, name));
//        ResourcePack resourcePack = new ResourcePack(FilenameUtils.getBaseName(name));
//        for (StoredData storedData : storedDataList) {
//            size += storedData.getSize();
//            if (updatedDate.isBefore(storedData.getUpdatedDate())) {
//                updatedDate = storedData.getUpdatedDate();
//            }
//            Resource<?> object = resourceCodeAdapter.toObject(compatibility(this, storedData));
//            resourcePack.put(object.getKey(), object);
//        }
//        K key = Serialization.deserialize(getKeyType(), FilenameUtils.getBaseName(name));
//        StoredData storedData = new StoredData(null, null, size, updatedDate);
//        return new OnDemandCacheItem<>(
//                ResourcePack.class, key, resourcePack, storedData, (ResourcePackCache<K>) getCache()
//        );
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public OnDemandCacheItem<K, ResourcePack> loadMetadataSingle(Plugin plugin, String directory, String name) {
//        CacheItem<K, ResourcePack> cacheItem = loadSingle(plugin, directory, name);
//        return new OnDemandCacheItem<>(
//                ResourcePack.class,
//                cacheItem.getKey(),
//                cacheItem.getData(),
//                cacheItem.getSize(),
//                cacheItem.getUpdatedDate(),
//                (ResourcePackCache<K>) getCache()
//        );
//    }
//
//    @Override
//    public void saveSingle(Plugin plugin, String directory, OnDemandCacheItem<K, ResourcePack> cacheItem) {
//        String name = Serialization.serialize(cacheItem.getKey());
//        List<StoredData> storedDataList = new ArrayList<>();
//        for (Map.Entry<String, Resource<?>> entry : cacheItem.getData().entrySet()) {
//            String data = resourceCodeAdapter.fromObject(entry.getValue());
//            storedDataList.add(new StoredData(entry.getKey(), data, 0, null));
//        }
//        storageAdapter.write(plugin, append(directory, name), storedDataList);
//    }
//
//    @Override
//    public void deleteSingle(Plugin plugin, String directory, K key) {
//        String name = Serialization.serialize(key);
//        storageAdapter.delete(plugin, append(directory, name));
//    }
//}
