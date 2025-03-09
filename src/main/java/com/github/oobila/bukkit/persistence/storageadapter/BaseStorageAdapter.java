package com.github.oobila.bukkit.persistence.storageadapter;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

public abstract class BaseStorageAdapter implements StorageAdapter {

    @Getter(AccessLevel.PROTECTED)
    private Plugin plugin;

    @Override
    public void register(Plugin plugin) {
        this.plugin = plugin;
    }

}
