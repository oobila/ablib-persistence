package com.github.oobila.bukkit.persistence;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Central registry of per-player caches. Register a {@link ReadCache} here so that
 * {@code PersistencePlayerJoinListener} can load it automatically when a player joins and
 * save/unload it when they quit, without the plugin having to hook the join/quit events itself.
 * This class is static/global by design: it exists for the lifetime of the server (not a single
 * plugin), so every persistence-backed plugin sharing this library gets consistent per-player
 * load/save behaviour for free.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class CacheManager {

    /** All caches that should be loaded/saved automatically as players join/leave. */
    @Getter
    private static final List<ReadCache<?, ?, ?>> playerReadCaches = new ArrayList<>();

    /**
     * Registers a cache to be loaded on player join and saved + unloaded on player quit.
     * Typically called once from a plugin's {@code onEnable()} for each per-player cache it owns.
     *
     * @param cache the cache to manage; its partition is expected to be a player's UUID
     */
    public static void registerPlayerCache(ReadCache<?, ?, ?> cache) {
        playerReadCaches.add(cache);
    }

}
