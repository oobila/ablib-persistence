package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

@Getter
public abstract class BaseCache<K, V> {

    @Getter
    @Setter
    private SqlConnectionProperties sqlConnectionProperties;

    private final String name;
    private final Class<K> keyType;
    private final Class<V> type;
    protected Plugin plugin;

    public BaseCache(String name, Class<K> keyType, Class<V> type) {
        this.name = name;
        this.keyType = keyType;
        this.type = type;
    }

    public abstract String getSubFolderName();
}