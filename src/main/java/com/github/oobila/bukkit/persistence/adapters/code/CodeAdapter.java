package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public interface CodeAdapter<V> {

    Plugin getPlugin();

    void setPlugin(Plugin plugin);

    Class<V> getType();

    default String getTypeName() {
        return getType().getName();
    }

    Map<String, V> toObjects(StoredData storedData);

    String fromObjects(Map<String, V> map);

}
