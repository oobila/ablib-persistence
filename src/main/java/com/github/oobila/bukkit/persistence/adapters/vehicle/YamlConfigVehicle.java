//package com.github.oobila.bukkit.persistence.adapters.vehicle;
//
//import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
//import com.github.oobila.bukkit.persistence.adapters.code.DummyCodeAdapter;
//import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
//import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
//import com.github.oobila.bukkit.persistence.adapters.utils.MyYamlConfiguration;
//import com.github.oobila.bukkit.persistence.model.CacheItem;
//import com.github.oobila.bukkit.persistence.serializers.Serialization;
//import lombok.Getter;
//import org.bukkit.configuration.InvalidConfigurationException;
//import org.bukkit.plugin.Plugin;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//
//import static com.github.oobila.bukkit.common.ABCommon.log;
//import static com.github.oobila.bukkit.persistence.utils.BackwardsCompatibilityUtil.compatibility;
//
//@SuppressWarnings("unused")
//@Getter
//public class YamlConfigVehicle<K, V, C extends CacheItem<K, V>> extends BasePersistenceVehicle<K, V, C> {
//
//    private final Class<K> keyType;
//    private final StorageAdapter storageAdapter;
//    private final DummyCodeAdapter<V> codeAdapter;
//
//    public YamlConfigVehicle(Class<K> keyType, Class<V> valueType, StorageAdapter storageAdapter) {
//        this.keyType = keyType;
//        this.storageAdapter = storageAdapter;
//        this.codeAdapter = new DummyCodeAdapter<>(valueType);
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public Map<K, C> load(Plugin plugin, String directory) {
//        try {
//            Map<K, C> map = new HashMap<>();
//            List<StoredData> storedDataList = storageAdapter.read(plugin, directory);
//            for (StoredData storedData : storedDataList) {
//                storedData = compatibility(this, storedData);
//                MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
//
//                yamlConfiguration.loadFromString(storedData.getData());
//                Map<String, Object> objects = yamlConfiguration.getValues(false);
//                for (Map.Entry<String, Object> entry : objects.entrySet()) {
//                    K key = Serialization.deserialize(getKeyType(), entry.getKey());
//                    V value = (V) entry.getValue();
//                    C cacheItem = (C) new CacheItem<>(
//                            this.getCodeAdapter().getType(), key, value, storedData
//                    );
//                    map.put(key, cacheItem);
//                }
//            }
//            return map;
//        } catch (InvalidConfigurationException e) {
//            log(Level.SEVERE, "Could not load Yaml from: {0}", directory);
//            log(Level.SEVERE, e);
//            throw new PersistenceRuntimeException(e);
//        }
//    }
//
//    @Override
//    public void save(Plugin plugin, String directory, Map<K, C> map) {
//        MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
//        map.forEach((key, value) -> {
//            String name = Serialization.serialize(key);
//            yamlConfiguration.set(name, value.getData());
//        });
//        String data = yamlConfiguration.saveToString();
//        StoredData storedData = new StoredData(directory, data, 0, null);
//        storageAdapter.write(plugin, directory, List.of(storedData));
//    }
//}