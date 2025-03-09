package com.github.oobila.bukkit.persistence.old.vehicles.global;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.old.adapters.code.DummyCodeAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.utils.MyYamlConfiguration;
import com.github.oobila.bukkit.persistence.old.model.CacheItem;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;
import static com.github.oobila.bukkit.persistence.old.vehicles.utils.BackwardsCompatibilityUtil.compatibility;

@SuppressWarnings("unused")
@Getter
public class YamlConfigVehicle<K, V, C extends CacheItem<K, V>> extends BaseGlobalPersistenceVehicle<K, V, C> {

    private final StorageAdapter storageAdapter;
    private final DummyCodeAdapter<V> codeAdapter;

    public YamlConfigVehicle(Class<K> keyType, Class<V> valueType, StorageAdapter storageAdapter) {
        super(keyType);
        this.storageAdapter = storageAdapter;
        this.codeAdapter = new DummyCodeAdapter<>(valueType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, C> load() {
        try {
            Map<K, C> map = new HashMap<>();
            List<StoredData> storedDataList = storageAdapter.read(getPlugin(), getCache().getName());
            for (StoredData storedData : storedDataList) {
                storedData = compatibility(this, storedData);
                MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();

                yamlConfiguration.loadFromString(storedData.getData());
                Map<String, Object> objects = yamlConfiguration.getValues(false);
                for (Map.Entry<String, Object> entry : objects.entrySet()) {
                    K key = Serialization.deserialize(getKeyType(), entry.getKey());
                    V value = (V) entry.getValue();
                    C cacheItem = (C) new CacheItem<>(
                            this.getCodeAdapter().getType(), key, value, storedData
                    );
                    map.put(key, cacheItem);
                }
            }
            return map;
        } catch (InvalidConfigurationException e) {
            log(Level.SEVERE, "Could not load Yaml from: {0}", getCache().getName());
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public void save(Map<K, C> map) {
        MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
        map.forEach((key, value) -> {
            String name = Serialization.serialize(key);
            yamlConfiguration.set(name, value.getData());
        });
        String data = yamlConfiguration.saveToString();
        StoredData storedData = new StoredData(getCache().getName(), data, 0, null);
        storageAdapter.write(getPlugin(), getCache().getName(), List.of(storedData));
    }
}