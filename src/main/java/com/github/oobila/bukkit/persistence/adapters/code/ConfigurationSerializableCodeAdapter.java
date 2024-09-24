package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Getter
public class ConfigurationSerializableCodeAdapter<T> implements CodeAdapter<T> {

    private final Class<T> type;

    @Override
    public T toObject(StoredData storedData) {
        try {
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.loadFromString(storedData.getData());
            Map<String, Object> map = yamlConfiguration.getValues(false);
            return type.cast(type.getDeclaredMethod("deserialize", Map.class).invoke(null, map));
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
    public String fromObject(T object) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) type.getDeclaredMethod("serialize").invoke(object);
            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            map.forEach(yamlConfiguration::set);
            return yamlConfiguration.saveToString();
        } catch (ClassCastException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            log(Level.SEVERE, "Could not save object for type: {0}. Bad class setup.", getTypeName());
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }
}
