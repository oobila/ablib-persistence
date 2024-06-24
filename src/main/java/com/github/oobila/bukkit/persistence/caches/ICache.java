package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.persistence.CacheManager;
import org.bukkit.plugin.Plugin;

public interface ICache {

    void onOpen(Plugin plugin);
    void onClose();
    default void open(Plugin plugin) {
        onOpen(plugin);
        CacheManager.addCache(this);
    }
    default void close() {
        onClose();
    }

}
