package com.github.oobila.bukkit.persistence.old.vehicles;

import com.github.oobila.bukkit.persistence.old.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.old.model.BackwardsCompatibility;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import com.github.oobila.bukkit.persistence.old.vehicles.pollmethod.PollMethod;
import com.github.oobila.bukkit.persistence.old.vehicles.readmethod.PersistenceReadMethod;
import com.github.oobila.bukkit.persistence.old.vehicles.utils.CacheItemFactory;
import lombok.Builder;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.oobila.bukkit.persistence.old.vehicles.utils.BackwardsCompatibilityUtil.compatibility;

@Builder
public class CacheVehicle<K, V, C extends CacheItem<K, V>> {

    private Plugin plugin;
    private final Class<K> keyType;
    private final Class<V> valueType;
    private final PollMethod pollMethod;
    private final StorageAdapter storageAdapter;
    private final PersistenceReadMethod readMethod;
    private final CacheItemFactory<K, V, C> cacheItemFactory;
    private final CodeAdapter<V> codeAdapter;
    private final List<BackwardsCompatibility> backwardsCompatibilities;

    public CacheVehicle(Class<K> keyType, Class<V> valueType, PollMethod pollMethod, StorageAdapter storageAdapter,
                        PersistenceReadMethod readMethod, CacheItemFactory<K, V, C> cacheItemFactory,
                        CodeAdapter<V> codeAdapter) {
        this(keyType, valueType, pollMethod, storageAdapter, readMethod, cacheItemFactory,
                codeAdapter, new ArrayList<>());
    }

    public CacheVehicle(Class<K> keyType, Class<V> valueType, PollMethod pollMethod, StorageAdapter storageAdapter,
                        PersistenceReadMethod readMethod, CacheItemFactory<K, V, C> cacheItemFactory,
                        CodeAdapter<V> codeAdapter, List<BackwardsCompatibility> backwardsCompatibilities) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.pollMethod = pollMethod;
        this.storageAdapter = storageAdapter;
        this.readMethod = readMethod;
        this.cacheItemFactory = cacheItemFactory;
        this.codeAdapter = codeAdapter;
        this.backwardsCompatibilities = backwardsCompatibilities;
    }

    public void register(Plugin plugin) {
        this.plugin = plugin;
    }

    public Map<K, C> load(Object object) {
        List<String> paths = pollMethod.getPaths(storageAdapter, object);
        List<StoredData> storedData = new ArrayList<>();
        paths.forEach(pathString -> storedData.addAll(storageAdapter.read(plugin, pathString)));
        Map<K, C> map = new HashMap<>();
        storedData.forEach(data -> {
            data = compatibility(backwardsCompatibilities, data);
            K key = Serialization.deserialize(keyType, data.getName());
            V value = codeAdapter.toObject(data);
            C cacheItem = cacheItemFactory.newItem(valueType, key, value, data);
            map.put(key, cacheItem);
        });
        return map;
    }

    public void store(Map<K, C> map, Object object) {
        Map<String, List<StoredData>> storedDataMap = new HashMap<>();
        map.forEach((key, value) -> {
            String name = Serialization.serialize(value.getKey());
            String data = codeAdapter.fromObject(value.getData());
            String path = pollMethod.getPath(storageAdapter, object, value);
            StoredData storedData = new StoredData(name, data, 0, null);
            storedDataMap.putIfAbsent(path, new ArrayList<>());
            storedDataMap.get(path).add(storedData);
        });
        storedDataMap.forEach((path, storedDataList) -> storageAdapter.write(plugin, path, storedDataList));
    }
}