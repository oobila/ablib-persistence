package com.github.oobila.bukkit.persistence.caches;

import org.bukkit.plugin.Plugin;

public interface ICache {

    void open(Plugin plugin);
    void close();

}
