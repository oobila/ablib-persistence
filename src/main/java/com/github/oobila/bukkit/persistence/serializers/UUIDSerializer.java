package com.github.oobila.bukkit.persistence.serializers;

import java.util.UUID;

public class UUIDSerializer implements Serializer<UUID> {

    @Override
    public String serialize(UUID object) {
        return object.toString();
    }

    @Override
    public UUID deserialize(String string) {
        return UUID.fromString(string);
    }
}
