package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.chat.Message;
import com.github.oobila.bukkit.persistence.CacheManager;
import com.github.oobila.bukkit.persistence.caches.real.model.MessageItem;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import com.github.oobila.bukkit.persistence.model.SqlConnectionProperties;
import com.github.oobila.bukkit.persistence.observers.PlayerObserver;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Comparator;
import java.util.UUID;

public class MessageCache extends CombiCache<UUID, MessageItem> implements PlayerObserver {

    public MessageCache(Plugin plugin, String pathString, String tableName,
                        SqlConnectionProperties sqlConnectionProperties, boolean primaryIsSql) {
        super(plugin, UUID.class, MessageItem.class, pathString, tableName, sqlConnectionProperties, primaryIsSql);
        CacheManager.registerPlayerCache(this);
    }

    @Override
    public void onJoin(Player player) {
        values(player.getUniqueId()).stream()
                .map(CacheItem::getData)
                .sorted(Comparator.comparing(MessageItem::getDateTime))
                .map(MessageItem::getMessage)
                .forEach(message -> Message.builder(message).send(player));
    }

    @Override
    public void onLeave(OfflinePlayer player) {
        //do nothing
    }
}
