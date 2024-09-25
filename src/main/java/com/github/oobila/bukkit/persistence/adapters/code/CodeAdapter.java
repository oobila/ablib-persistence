package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import org.bukkit.plugin.Plugin;

public interface CodeAdapter<T> {

    Plugin getPlugin();
    void setPlugin(Plugin plugin);
    Class<T> getType();
    default String getTypeName() {
        return getType().getName();
    }
    T toObject(StoredData storedData);
    String fromObject(T object);

}
