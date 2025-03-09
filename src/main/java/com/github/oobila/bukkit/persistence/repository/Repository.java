package com.github.oobila.bukkit.persistence.repository;

import com.github.oobila.bukkit.persistence.repository.cache.Cache;
import com.github.oobila.bukkit.persistence.codeadapter.CodeAdapter;
import com.github.oobila.bukkit.persistence.codeadapter.MultiCodeAdapter;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import com.github.oobila.bukkit.persistence.storageadapter.StorageAdapter;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class Repository<K, V> {

    private static final Pattern KEY_PATTERN = Pattern.compile("[a-zA-Z0-9_\\-]*\\{key\\}[a-zA-Z0-9_\\-.]*");

    private final Class<K> keyType;
    private final Class<V> valueType;
    private final Cache<K, V> cache;
    private final CodeAdapter<?> codeAdapter;
    private final StorageAdapter storageAdapter;
    private final String path;
    private final boolean pathContainsPartition;
    private final boolean pathContainsKey;
    private final boolean pathContainsZip;
    private Plugin plugin;


    public Repository(Class<K> keyType, Class<V> valueType, Cache<K, V> cache, CodeAdapter<?> codeAdapter, StorageAdapter storageAdapter, String path) {
        this.keyType = keyType;
        this.valueType = valueType;
        this.cache = cache;
        this.codeAdapter = codeAdapter;
        this.storageAdapter = storageAdapter;
        this.path = path;
        this.pathContainsPartition = path.contains("{uuid}");
        this.pathContainsKey = path.contains("{key}");
        this.pathContainsZip = path.contains("{zip}");
    }

    public void load(Plugin plugin) {
        this.plugin = plugin;
        load((UUID) null);
    }

    @SuppressWarnings("unchecked")
    public void load(UUID partition) {
        if (cache != null) {
            if (pathContainsKey) {
                Map<K, V> map = new HashMap<>();
                for (String key : storageAdapter.poll(formatPath(getParentOfPath(path), partition, (String) null))) {
                    V item = (V) codeAdapter.deserialize(storageAdapter.read(formatPath(path, partition, key)));
                    map.put(Serialization.deserialize(keyType, key), item);
                }
                cache.putPartition(partition, map);
            } else {
                Map<K, V> map = (Map<K, V>) codeAdapter.deserialize(storageAdapter.read(formatPath(path, partition, (String) null)));
                cache.putPartition(partition, map);
            }
        }
    }

    public void unload() {
        unload(null);
    }

    public void unload(UUID partition) {
        if (cache != null) {
            cache.unload(partition);
        }
    }

    @SuppressWarnings("unchecked")
    public V get(UUID partition, K key) {
        if (cache != null) {
            return cache.get(partition, key);
        } else {
            if (pathContainsKey) {
                return (V) codeAdapter.deserialize(storageAdapter.read(formatPath(path, partition, key)));
            } else {
                Map<K, V> map = (Map<K, V>) codeAdapter.deserialize(storageAdapter.read(formatPath(path, partition, key)));
                return map.get(key);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void put(UUID partition, K key, V value) {
        if (cache != null) {
            cache.put(partition, key, value);
        } else {
            if (pathContainsKey) {
                storageAdapter.write(formatPath(path, partition, key), ((CodeAdapter<V>) codeAdapter).serialize(value));
            } else {
                Map<K, V> map = (Map<K, V>) codeAdapter.deserialize(storageAdapter.read(formatPath(path, partition, key)));
                map.put(key, value);
                storageAdapter.write(formatPath(path, partition, key), ((MultiCodeAdapter<K, V>) codeAdapter).serialize(map));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void remove(UUID partition, K key) {
        if (cache != null) {
            cache.remove(partition, key);
        } else {
            if (pathContainsKey) {
                storageAdapter.delete(formatPath(path, partition, key));
            } else {
                Map<K, V> map = (Map<K, V>) codeAdapter.deserialize(storageAdapter.read(formatPath(path, partition, key)));
                map.remove(key);
                storageAdapter.write(formatPath(path, partition, key), ((MultiCodeAdapter<K, V>) codeAdapter).serialize(map));
            }
        }
    }

    private String formatPath(String path, UUID partition, K key) {
        return formatPath(path, partition, Serialization.serialize(key));
    }

    private String formatPath(String path, UUID partition, String key) {
        String p = path;
        if (partition != null) {
            p = p.replace("{uuid}", partition.toString());
        }
        if (key != null) {
            p = p.replace("{key}", key);
        }
        return p;
    }

    private String getParentOfPath(String path) {
        String keyMatch = KEY_PATTERN.matcher(path).group();
        return path.replace(keyMatch, "");
    }

}
