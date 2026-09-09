package com.github.oobila.bukkit.persistence.serializers;

import java.util.UUID;

/** {@link KeySerializer} for {@code UUID} keys, via {@code UUID.toString()}/{@code fromString()}. */
public class UUIDSerializer implements KeySerializer<UUID> {

    @Override
    public String serialize(UUID object) {
        return object.toString();
    }

    @Override
    public UUID deserialize(String string) {
        return UUID.fromString(string);
    }
}
