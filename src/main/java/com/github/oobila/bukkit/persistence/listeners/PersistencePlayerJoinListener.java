package com.github.oobila.bukkit.persistence.listeners;

import com.github.oobila.bukkit.persistence.caches.exemplar.MessageCache;
import com.github.oobila.bukkit.persistence.model.MessageQueue;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class PersistencePlayerJoinListener implements Listener {

    private final MessageCache messageCache;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        MessageQueue queue = messageCache.get(event.getPlayer());
        if (queue != null) {
            queue.forEach(message -> event.getPlayer().sendMessage(message));
        }
    }

}
