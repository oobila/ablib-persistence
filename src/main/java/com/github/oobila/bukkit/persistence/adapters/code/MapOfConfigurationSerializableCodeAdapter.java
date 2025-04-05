package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.adapters.utils.MyYamlConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Getter
public class MapOfConfigurationSerializableCodeAdapter<V> implements CodeAdapter<V> {

    private final Class<V> type;

    @Setter
    private Plugin plugin;

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, V> toObjects(StoredData storedData) {
        try {
            MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
            yamlConfiguration.loadFromString(storedData.getData());
            Map<String, Object> map = yamlConfiguration.getValues(false);
            Map<String, V> retMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                retMap.put(
                        entry.getKey(),
                        !ConfigurationSerializable.class.isAssignableFrom(type) ? (V) entry.getValue() :
                                type.cast(type.getDeclaredMethod("deserialize", type).invoke(null, entry.getValue()))
                );
            }
            return retMap;
        } catch (InvalidConfigurationException e) {
            log(Level.SEVERE, "Could not load object for type: {0}. Could not read data", getTypeName());
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        } catch (ClassCastException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            log(Level.SEVERE, "Could not load object for type: {0}. Bad class setup.", getTypeName());
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public String fromObjects(Map<String, V> map) {
        try {
            MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration();
            for (Map.Entry<String, V> entry : map.entrySet()) {
                yamlConfiguration.set(
                        entry.getKey(),
                        !ConfigurationSerializable.class.isAssignableFrom(type) ? entry.getValue() :
                                type.getDeclaredMethod("serialize").invoke(entry.getValue())
                );
            }
            return yamlConfiguration.saveToString();
        } catch (ClassCastException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            log(Level.SEVERE, "Could not save object for type: {0}. Bad class setup.", getTypeName());
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }
}
