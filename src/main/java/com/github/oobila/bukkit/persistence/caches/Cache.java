package com.github.oobila.bukkit.persistence.caches;

import org.bukkit.plugin.Plugin;

public interface Cache {

    String getPathString();

    Plugin getPlugin();

}
