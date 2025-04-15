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

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.UUID;

public class MessageCache extends CombiCache<UUID, MessageItem> implements PlayerObserver {

    public MessageCache(Plugin plugin, String pathString, String tableName,
                        SqlConnectionProperties sqlConnectionProperties, boolean primaryIsSql) {
        super(plugin, UUID.class, MessageItem.class, pathString, tableName, sqlConnectionProperties, primaryIsSql);
        CacheManager.registerPlayerCache(this);
    }

    public void message(OfflinePlayer player, String text, String... args) {
        putValue(
                player.getUniqueId(),
                UUID.randomUUID(),
                new MessageItem(
                        new Message(text, args).toString(),
                        ZonedDateTime.now()
                ),
                a -> {}
        );
    }

    public void message(OfflinePlayer player, String text) {
        putValue(
                player.getUniqueId(),
                UUID.randomUUID(),
                new MessageItem(
                        new Message(text).toString(),
                        ZonedDateTime.now()
                ),
                a -> {}
        );
    }

    @Override
    public void onJoin(Player player) {
        values(player.getUniqueId()).stream()
                .map(CacheItem::getData)
                .sorted(Comparator.comparing(MessageItem::getDateTime))
                .map(MessageItem::getMessage)
                .forEach(player::sendMessage);
    }

    @Override
    public void onLeave(OfflinePlayer player) {
        //do nothing
    }
}
