/**
 * Optional listener interfaces a plugin can register on a cache (via its
 * {@code addObserver(...)} methods) to react to cache lifecycle events (load/save/unload) and
 * individual record operations (put/remove) without modifying the cache itself. See
 * {@link com.github.oobila.bukkit.persistence.observers.PlayerObserver} for the join/leave
 * convenience used by per-player caches such as {@code MessageCache}.
 */
package com.github.oobila.bukkit.persistence.observers;
