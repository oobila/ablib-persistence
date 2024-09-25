package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DirectoryUtils {

    public static String append(String directory, String item) {
        return String.format("%s/%s", directory, item);
    }

    public static String playerDir(String base, UUID playerId, String item) {
        return String.format("%s%s/%s", base, Serialization.serialize(playerId), item);
    }

}
