package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.github.oobila.bukkit.persistence.adapters.utils.MyYamlConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

/**
 * (De)serializes a single {@code ConfigurationSerializable} value (e.g. an {@code ItemStack}) to
 * and from YAML, via Bukkit's own {@code serialize()}/{@code deserialize(Map)} contract. Unlike
 * {@link MapOfConfigurationSerializableCodeAdapter}, this stores exactly one value per record
 * rather than a whole map of them — used by {@code SimpleSqlCache}, where each row already
 * corresponds to a single value.
 *
 * @param <V> the {@code ConfigurationSerializable} type this adapter (de)serializes
 */
@SuppressWarnings("unused")
@Getter
public class ConfigurationSerializableCodeAdapter<V> implements CodeAdapter<V> {

    private final Class<V> type;
    private final boolean includeDataHeader;

    @Setter
    private Plugin plugin;

    /**
     * @param type              the value type; automatically registered with Bukkit's
     *                           {@code ConfigurationSerialization} if it implements
     *                           {@code ConfigurationSerializable}
     * @param includeDataHeader whether to prepend the "generated file, do not edit" YAML header
     *                           (see {@link com.github.oobila.bukkit.persistence.adapters.utils.MyYamlConfiguration})
     */
    @SuppressWarnings("unchecked")
    public ConfigurationSerializableCodeAdapter(Class<V> type, boolean includeDataHeader) {
        this.type = type;
        this.includeDataHeader = includeDataHeader;
        if (ConfigurationSerializable.class.isAssignableFrom(type)) {
            ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) type);
        }
    }

    @Override
    public Map<String, V> toObjects(StoredData storedData) {
        try {
            MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration(includeDataHeader);
            yamlConfiguration.loadFromString(storedData.getData());
            Map<String, Object> map = yamlConfiguration.getValues(false);
            return Map.of(
                    Strings.EMPTY,
                    type.cast(type.getDeclaredMethod("deserialize", Map.class).invoke(null, map))
            );
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
    public String fromObjects(Map<String, V> inMap) {
        try {
            Object object = inMap.values().iterator().next();
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) type.getDeclaredMethod("serialize").invoke(object);
            MyYamlConfiguration yamlConfiguration = new MyYamlConfiguration(includeDataHeader);
            map.forEach(yamlConfiguration::set);
            return yamlConfiguration.saveToString();
        } catch (ClassCastException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            log(Level.SEVERE, "Could not save object for type: {0}. Bad class setup.", getTypeName());
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }
}
