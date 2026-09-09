package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import org.bukkit.plugin.Plugin;

import java.util.Map;

/**
 * (De)serializes value objects of type {@code V} to and from the plain-text form stored by a
 * {@link com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter}. Implementations
 * exist for Bukkit's {@code ConfigurationSerializable} objects (YAML), plain strings, WorldEdit
 * clipboards, and zipped resource bundles — see the classes in this package.
 * <p>
 * Methods take/return a {@code Map<String, V>} rather than a single {@code V} because some
 * storage layouts (e.g. one file holding many keyed records) pack several values into a single
 * piece of stored text; adapters that only ever handle one value at a time use the single empty
 * string key ({@code Strings.EMPTY}) as a placeholder.
 *
 * @param <V> the value type this adapter (de)serializes
 */
public interface CodeAdapter<V> {

    Plugin getPlugin();

    void setPlugin(Plugin plugin);

    /** The runtime type of value this adapter produces from {@link #toObjects}. */
    Class<V> getType();

    default String getTypeName() {
        return getType().getName();
    }

    /** Deserializes stored text into one or more value objects, keyed by record name. */
    Map<String, V> toObjects(StoredData storedData);

    /** Serializes one or more value objects into the text form to be persisted. */
    String fromObjects(Map<String, V> map);

}
