package com.github.oobila.bukkit.persistence.listeners;

import com.github.oobila.bukkit.persistence.CacheManager;
import com.github.oobila.bukkit.persistence.caches.AsyncPlayerCache;
import com.github.oobila.bukkit.persistence.caches.IPlayerCache;
import com.github.oobila.bukkit.persistence.caches.exemplar.MessageCache;
import com.github.oobila.bukkit.persistence.model.MessageQueue;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
@SuppressWarnings("java:S1871")
public class PersistencePlayerJoinListener implements Listener {

    private final MessageCache messageCache;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MessageQueue queue = messageCache.get(event.getPlayer());
        if (queue != null) {
            queue.forEach(message -> event.getPlayer().sendMessage(message));
        }

        CacheManager.getPlayerCaches().forEach(cache -> {
            if (cache instanceof IPlayerCache<?,?> playerCache) {
                playerCache.open(event.getPlayer());
            } else if (cache instanceof AsyncPlayerCache<?,?> playerCache) {
                playerCache.open(event.getPlayer());
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CacheManager.getPlayerCaches().forEach(cache -> {
            if (cache instanceof IPlayerCache<?,?> playerCache) {
                playerCache.close(event.getPlayer());
            } else if (cache instanceof AsyncPlayerCache<?,?> playerCache) {
                playerCache.close(event.getPlayer());
            }
        });
    }

}
