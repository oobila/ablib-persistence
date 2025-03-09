package com.github.oobila.bukkit.persistence.serializers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class OfflinePlayerSerializer implements Serializer<OfflinePlayer> {

    @Override
    public String serialize(OfflinePlayer object) {
        return object.getUniqueId().toString();
    }

    @Override
    public OfflinePlayer deserialize(String string) {
        return Bukkit.getOfflinePlayer(UUID.fromString(string));
    }
}
