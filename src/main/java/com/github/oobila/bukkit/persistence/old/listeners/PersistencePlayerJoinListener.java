package com.github.oobila.bukkit.persistence.old.listeners;

import com.github.oobila.bukkit.persistence.old.CacheManager;
import com.github.oobila.bukkit.persistence.old.caches.async.AsyncPlayerReadCache;
import com.github.oobila.bukkit.persistence.old.caches.async.AsyncPlayerWriteCache;
import com.github.oobila.bukkit.persistence.old.caches.standard.StandardPlayerReadCache;
import com.github.oobila.bukkit.persistence.old.caches.standard.StandardPlayerWriteCache;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
@SuppressWarnings("java:S1871")
public class PersistencePlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CacheManager.getPlayerReadCaches().forEach(playerReadCache -> {
            //LOAD
            if (playerReadCache instanceof StandardPlayerReadCache<?, ?, ?> standardPlayerReadCache) {
                standardPlayerReadCache.loadPlayer(event.getPlayer().getUniqueId());
            } else if (playerReadCache instanceof AsyncPlayerReadCache<?, ?, ?> asyncPlayerReadCache) {
                asyncPlayerReadCache.loadPlayer(event.getPlayer().getUniqueId(), null);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CacheManager.getPlayerReadCaches().forEach(playerReadCache -> {
            //SAVE
            if (playerReadCache instanceof StandardPlayerWriteCache<?, ?, ?> standardPlayerWriteCache) {
                standardPlayerWriteCache.savePlayer(event.getPlayer().getUniqueId());
            } else if (playerReadCache instanceof AsyncPlayerWriteCache<?, ?, ?> asyncPlayerWriteCache) {
                asyncPlayerWriteCache.savePlayer(event.getPlayer().getUniqueId(), null);
            }
            //UNLOAD
            if (playerReadCache instanceof StandardPlayerReadCache<?, ?, ?> standardPlayerReadCache) {
                standardPlayerReadCache.unloadPlayer(event.getPlayer().getUniqueId());
            } else if (playerReadCache instanceof AsyncPlayerReadCache<?, ?, ?> asyncPlayerReadCache) {
                asyncPlayerReadCache.unloadPlayer(event.getPlayer().getUniqueId());
            }
        });
    }

}
