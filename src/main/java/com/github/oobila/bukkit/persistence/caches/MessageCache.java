package com.github.oobila.bukkit.persistence.caches;

import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.persistence.model.MessageQueue;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class MessageCache extends DataCache<OfflinePlayer, MessageQueue> {

    private static final String SUB_FOLDER_NAME = "data";

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
        return SUB_FOLDER_NAME;
    }
}
