package com.github.oobila.bukkit.persistence.serializers;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ZonedDateTimeSerializer implements KeySerializer<ZonedDateTime> {

    @Override
    public String serialize(ZonedDateTime object) {
        return Long.toString(object.toInstant().toEpochMilli());
    }

    @Override
    public ZonedDateTime deserialize(String string) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(string)), ZoneOffset.UTC);
    }
}
