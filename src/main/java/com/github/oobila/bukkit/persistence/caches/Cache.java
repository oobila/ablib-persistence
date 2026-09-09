package com.github.oobila.bukkit.persistence.caches;

import org.bukkit.plugin.Plugin;

/**
 * Root marker interface for every cache in this library. A cache is the object plugin code
 * actually interacts with to store and retrieve data; it hides the details of where/how that
 * data is physically persisted (see {@link ReadCache}/{@link WriteCache} for the operations that
 * build on this, and the {@code caches.standard}/{@code caches.async}/{@code caches.real}
 * packages for concrete implementations).
 */
public interface Cache {

    /** The path/table template this cache's write vehicle stores data under (e.g. for logging/debugging). */
    String getPathString();

    /** The plugin this cache is currently associated with (set on {@code load(Plugin)}). */
    Plugin getPlugin();

}
