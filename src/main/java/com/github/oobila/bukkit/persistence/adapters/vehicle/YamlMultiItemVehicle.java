package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.persistence.utils.BackwardsCompatibilityUtil.compatibility;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Getter
public class YamlMultiItemVehicle<K, V> extends BasePersistenceVehicle<K, V> {

    private final Class<K> keyType;
    private final StorageAdapter storageAdapter;
    private final CodeAdapter<V> codeAdapter;

    @Override
    public Map<K, CacheItem<K,V>> load(Plugin plugin, String directory) {
        try {
            Map<K, CacheItem<K,V>> map = new HashMap<>();
            List<StoredData> storedDataList = storageAdapter.read(plugin, directory);
            for (StoredData storedData : storedDataList) {
                YamlConfiguration yamlConfiguration = new YamlConfiguration();
                yamlConfiguration.loadFromString(storedData.getData());
                Map<String, Object> objects = yamlConfiguration.getValues(false);
                objects.forEach((name, object) -> {
                    StoredData item = new StoredData(name, (String) object, 0, storedData.getUpdatedDate());
                    K key = Serialization.deserialize(getKeyType(), name);
                    V value = codeAdapter.toObject(compatibility(this, item));
                    CacheItem<K,V> cacheItem = new CacheItem<>(key, value, storedData);
                    map.put(key, cacheItem);
                });
            }
            return map;
        } catch (InvalidConfigurationException e) {
            log(Level.SEVERE, "Could not load Yaml from: {}", directory);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public void save(Plugin plugin, String directory, Map<K, CacheItem<K,V>> map) {
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        map.forEach((key, value) -> {
            String name = Serialization.serialize(key);
            String data = codeAdapter.fromObject(value.getData());
            yamlConfiguration.set(name, data);
        });
        String data = yamlConfiguration.saveToString();
        StoredData storedData = new StoredData(directory, data, 0, null);
        storageAdapter.write(plugin, directory, List.of(storedData));
    }

    @Override
    public void saveSingle(Plugin plugin, String directory, CacheItem<K, V> cacheItem) {
        log(Level.WARNING, "Unsupported operation. saveSingle attempted on YamlMultiItemVehicle");
    }
}
