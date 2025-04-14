package com.github.oobila.bukkit.persistence.listeners;

import com.github.oobila.bukkit.persistence.CacheManager;
import com.github.oobila.bukkit.persistence.caches.WriteCache;
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
        CacheManager.getPlayerReadCaches().forEach(readCache ->
            //LOAD
            readCache.load(event.getPlayer().getUniqueId())
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        CacheManager.getPlayerReadCaches().forEach(readCache -> {
            //SAVE
            if (readCache instanceof WriteCache<?, ?, ?> writeCache) {
                writeCache.save(event.getPlayer().getUniqueId());
            }

            //UNLOAD
            readCache.unload(event.getPlayer().getUniqueId());
        });
    }

}
