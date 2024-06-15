package com.github.oobila.bukkit.persistence.caches.exemplar;

import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.persistence.caches.DataCache;
import com.github.oobila.bukkit.persistence.model.MessageQueue;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static com.github.oobila.bukkit.persistence.Constants.DATA;

public class MessageCache extends DataCache<OfflinePlayer, MessageQueue> {

    public MessageCache() {
        super("message-cache", OfflinePlayer.class, MessageQueue.class);
    }

    public void addMessage(OfflinePlayer player, Message message) {
        addMessage(player, message.toString());
    }

    public void addMessage(OfflinePlayer player, String message) {
        if (player.isOnline()) {
            Player p = player.getPlayer();
            p.sendMessage(message);
        }
    }

    @Override
    public String getSubFolderName() {
        return DATA;
    }
}
