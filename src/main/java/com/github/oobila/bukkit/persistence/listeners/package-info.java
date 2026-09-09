/**
 * Bukkit event listeners that drive the lifecycle of per-player caches automatically. Register
 * {@link com.github.oobila.bukkit.persistence.listeners.PersistencePlayerJoinListener} once per
 * plugin and every cache registered via
 * {@link com.github.oobila.bukkit.persistence.CacheManager#registerPlayerCache(com.github.oobila.bukkit.persistence.caches.ReadCache)}
 * will have its data loaded on player join and saved/unloaded on player quit, with no further
 * wiring required.
 */
package com.github.oobila.bukkit.persistence.listeners;
