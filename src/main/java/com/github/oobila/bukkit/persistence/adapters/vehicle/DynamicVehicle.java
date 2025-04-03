package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.common.utils.JavaUtil;
import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DynamicVehicle<K, V> extends BasePersistenceVehicle<K, V> {

    private static final String PARTITION_STRING = "{uuid}";
    private static final String PARTITION_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    private static final String KEY_STRING = "{key}";
    private static final String KEY_PATTERN = "[0-9a-zA-Z-_]+";
    private static final String SEPARATOR = "/";

    @Getter
    private final String pathString;
    private final String[] pathParts;
    private final boolean pathStringIncludesPartition;
    private final boolean pathStringIncludesKey;
    private final boolean isOnDemand;
    @Getter
    private final Class<K> keyType;
    private Plugin plugin;
    @Getter
    private final StorageAdapter storageAdapter;
    @Getter
    private final CodeAdapter<V> codeAdapter;
    @Setter
    private WriteCache<K, V> writeCache;

    public DynamicVehicle(String pathString, boolean isOnDemand, Class<K> keyType,
                          StorageAdapter storageAdapter, CodeAdapter<V> codeAdapter) {
        this.pathString = pathString;
        this.pathParts = pathString.split(SEPARATOR);
        String lower = pathString.toLowerCase();
        this.pathStringIncludesPartition = lower.contains(PARTITION_STRING);
        this.pathStringIncludesKey = lower.contains(KEY_STRING);
        this.isOnDemand = isOnDemand;
        this.keyType = keyType;
        this.storageAdapter = storageAdapter;
        this.codeAdapter = codeAdapter;
    }

    @Override
    public Map<K, CacheItem<K, V>> load(Plugin plugin) {
        this.plugin = plugin;
        codeAdapter.setPlugin(plugin);
        if (pathStringIncludesPartition) {
            //operation not supported
            return new HashMap<>();
        }
        List<StoredData> storedData;
        if (pathStringIncludesKey) {
            List<String> paths = getPaths(null);
            storedData = paths.stream().map(this::read).collect(JavaUtil.toSingleton());
            paths.forEach(path -> storedData.addAll(read(path)));
        } else {
            storedData = read(pathString);
        }
        return constructCacheMap(null, storedData);
    }

    @Override
    public Map<K, CacheItem<K, V>> load(Plugin plugin, UUID partition) {
        this.plugin = plugin;
        codeAdapter.setPlugin(plugin);
        if (!pathStringIncludesPartition) {
            //operation not supported
            return new HashMap<>();
        }
        List<String> paths = getPaths(partition);
        List<StoredData> storedData = new ArrayList<>();
        paths.forEach(path -> storedData.addAll(read(path)));
        return constructCacheMap(partition, storedData);
    }

    @Override
    public CacheItem<K, V> load(Plugin plugin, UUID partition, K key) {
        this.plugin = plugin;
        codeAdapter.setPlugin(plugin);
        String name = pathString
                .replace(PARTITION_STRING, partition.toString())
                .replace(KEY_STRING, Serialization.serialize(key));
        if (storageAdapter.exists(plugin, name)) {
            List<StoredData> storedData = read(name);
            if (storedData.size() == 1) {
                return createCacheItem(partition, storedData.get(0));
            }
        }
        return null;
    }

    @Override
    public void copyDefaults() {
        if (!pathStringIncludesKey && !pathStringIncludesPartition && resourceExists(plugin) &&
                !getStorageAdapter().exists(plugin, pathString)) {
            getStorageAdapter().copyDefaults(plugin, pathString);
        }
    }

    private boolean resourceExists(Plugin plugin) {
        return plugin.getResource(pathString) != null;
    }

    private List<StoredData> read(String path) {
        if (isOnDemand) {
            return storageAdapter.readMetaData(plugin, path);
        } else {
            return storageAdapter.read(plugin, path);
        }
    }

    private Map<K, CacheItem<K, V>> constructCacheMap(UUID partition, List<StoredData> storedDataList) {
        Map<K, CacheItem<K, V>> map = new HashMap<>();
        storedDataList.forEach(storedData -> {
            CacheItem<K, V> cacheItem = createCacheItem(partition, storedData);
            map.put(cacheItem.getKey(), cacheItem);
        });
        return map;
    }

    private CacheItem<K, V> createCacheItem(UUID partition, StoredData storedData) {
        K key = Serialization.deserialize(keyType, storedData.getName());
        if (isOnDemand) {
            return new OnDemandCacheItem<>(codeAdapter.getType(), partition, key, null, storedData, writeCache);
        } else {
            V value = codeAdapter.toObject(storedData);
            return new CacheItem<>(codeAdapter.getType(), key, value, storedData);
        }
    }

    @Override
    public void save(Plugin plugin, Map<K, CacheItem<K, V>> map) {
        if (isOnDemand || pathStringIncludesPartition) {
            throw new RuntimeException("This operation is not supported!");
        }
        if (pathStringIncludesKey) {
            map.forEach((key, cacheItem) -> {
                StoredData storedData = toStoredData(cacheItem);
                storageAdapter.write(plugin, pathString.replace(KEY_STRING, storedData.getName()), List.of(storedData));
            });
        } else {
            List<StoredData> storedDataList = map.values().stream()
                    .map(this::toStoredData)
                    .toList();
            storageAdapter.write(plugin, pathString, storedDataList);
        }
    }

    @Override
    public void save(Plugin plugin, UUID partition, Map<K, CacheItem<K, V>> map) {
        if (isOnDemand || !pathStringIncludesPartition) {
            throw new RuntimeException("This operation is not supported!");
        }
        map.forEach((key, cacheItem) ->
            save(plugin, partition, key, cacheItem)
        );
    }

    @Override
    public void save(Plugin plugin, UUID partition, K key, CacheItem<K, V> cacheItem) {
        StoredData storedData = toStoredData(cacheItem);
        storageAdapter.write(
                plugin,
                pathString.replace(KEY_STRING, storedData.getName()).replace(PARTITION_STRING, partition.toString()),
                List.of(storedData));
    }

    @Override
    public void delete(Plugin plugin, UUID partition) {
        String name = pathString
                .replace(PARTITION_STRING, partition.toString());
        storageAdapter.delete(plugin, name);
    }

    @Override
    public void delete(Plugin plugin, UUID partition, K key) {
        String name = pathString
                .replace(PARTITION_STRING, partition.toString())
                .replace(KEY_STRING, Serialization.serialize(key));
        storageAdapter.delete(plugin, name);
    }

    private StoredData toStoredData(CacheItem<K, V> cacheItem) {
        return new StoredData(
                Serialization.serialize(cacheItem.getKey()),
                codeAdapter.fromObject(cacheItem.getData()),
                0,
                null
        );
    }

    private String getPath(UUID partition, String key) {
        return pathString
                .replace(PARTITION_STRING, partition.toString())
                .replace(KEY_STRING, key);
    }

    private List<String> getPaths(UUID partition) {
        return getPaths(0, "", null, partition);
    }

    @SuppressWarnings("java:S3776")
    private List<String> getPaths(int depth, String current, List<String> paths, UUID partition) {
        if (depth >= pathParts.length) {
            return paths;
        }
        List<String> newPaths = new ArrayList<>();
        String pathPart = pathParts[depth];
        current = updateCurrent(depth, current, pathPart);
        if (pathPart.contains(PARTITION_STRING) || pathPart.contains(KEY_STRING)) {
            String pathPartRegex = pathPart
                    .replace(PARTITION_STRING, PARTITION_PATTERN)
                    .replace(KEY_STRING, KEY_PATTERN);
            if (paths == null || paths.isEmpty()) {
                List<String> options = storageAdapter.poll(plugin, current);
                for (String option : options) {
                    if (option.matches(pathPartRegex) &&
                            (!pathPart.contains(PARTITION_STRING) || option.contains(partition.toString()))) {
                        newPaths.add(option);
                    }
                }
            } else {
                for (String path : paths) {
                    List<String> options = storageAdapter.poll(plugin, current);
                    for (String option : options) {
                        if (option.matches(pathPartRegex) &&
                                (!pathPart.contains(PARTITION_STRING) || option.contains(partition.toString()))) {
                            newPaths.add(path + SEPARATOR + option);
                        }
                    }
                }
            }
        } else {
            if (paths == null || paths.isEmpty()) {
                newPaths.add(pathPart);
            } else {
                for (String path : paths) {
                    newPaths.add(path + SEPARATOR + pathPart);
                }
            }
        }
        return getPaths(++depth, current, newPaths, partition);
    }

    private String updateCurrent(int depth, String current, String pathPart) {
        if (depth == 0) {
            current += pathPart;
        } else {
            current += (SEPARATOR + pathPart);
        }
        return current;
    }
}
