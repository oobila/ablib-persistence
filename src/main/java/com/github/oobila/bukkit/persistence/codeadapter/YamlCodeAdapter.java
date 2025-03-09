package com.github.oobila.bukkit.persistence.codeadapter;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.codeadapter.model.StoredData;
import com.github.oobila.bukkit.persistence.serializers.Serialization;
import com.github.oobila.bukkit.persistence.utils.MyYamlConfiguration;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

public class YamlCodeAdapter<K, T> extends MultiCodeAdapter<K, T> {

    public YamlCodeAdapter(Class<K> keyType) {
        super(keyType);
    }

    @Override
    public StoredData serialize(Map<K, T> map) {
        MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
        map.forEach((key, value) -> {
            String name = Serialization.serialize(key);
            yamlConfiguration.set(name, value);
        });
        String data = yamlConfiguration.saveToString();
        return new StoredData(null, data, 0, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, T> deserialize(StoredData data) {
        try {
            Map<K, T> map = new HashMap<>();
            MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
            yamlConfiguration.loadFromString(data.getData());
            Map<String, Object> objects = yamlConfiguration.getValues(false);
            for (Map.Entry<String, Object> entry : objects.entrySet()) {
                K key = Serialization.deserialize(getKeyType(), entry.getKey());
                T value = (T) entry.getValue();
                map.put(key, value);
            }
            return map;
        } catch (InvalidConfigurationException e) {
            log(Level.SEVERE, "Could not load Yaml from: {0}", data.getPath());
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }
}
