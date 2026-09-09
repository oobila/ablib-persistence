package com.github.oobila.bukkit.persistence.listeners;

import com.github.oobila.bukkit.persistence.CacheManager;
import com.github.oobila.bukkit.persistence.caches.WriteCache;
import com.github.oobila.bukkit.persistence.observers.PlayerObserver;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Register this once per plugin (or once server-wide, if shared) to drive the lifecycle of every
 * cache registered via {@link CacheManager#registerPlayerCache}: their data is loaded when a
 * player joins and saved then unloaded when they quit, with {@link PlayerObserver} caches also
 * notified. Without this listener, per-player caches must be loaded/saved/unloaded manually.
 */
@AllArgsConstructor
@SuppressWarnings("java:S1871")
public class PersistencePlayerJoinListener implements Listener {

    /** Loads every registered cache's data for the joining player, notifying {@link PlayerObserver} caches. */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CacheManager.getPlayerReadCaches().forEach(readCache -> {
            //LOAD
            readCache.load(event.getPlayer().getUniqueId());

            //OBSERVE
            if (readCache instanceof PlayerObserver playerObserver) {
                playerObserver.onJoin(event.getPlayer());
            }
        });
    }

    /** Saves (if the cache supports writing) and unloads every registered cache's data for the leaving player, notifying {@link PlayerObserver} caches. */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CacheManager.getPlayerReadCaches().forEach(readCache -> {
            //SAVE
            if (readCache instanceof WriteCache<?, ?, ?> writeCache) {
                writeCache.save(event.getPlayer().getUniqueId());
            }

            //UNLOAD
            readCache.unload(event.getPlayer().getUniqueId());

            //OBSERVE
            if (readCache instanceof PlayerObserver playerObserver) {
                playerObserver.onLeave(event.getPlayer());
            }
        });
    }

}
