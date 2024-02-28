package com.github.oobila.bukkit.persistence.model.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateSerializeUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String serialize(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime().format(FORMATTER);
    }

    public static ZonedDateTime deserialize(String string) {
        return ZonedDateTime.of(
                LocalDateTime.from(FORMATTER.parse(string)),
                ZoneOffset.UTC
        );
    }

    public static ZonedDateTime deserialize(Long l) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(l),
                ZonedDateTime.now().getZone());
    }

    public static ZonedDateTime deserialize(Object o) {
        if (o instanceof String s) {
            return deserialize(s);
        } else if (o instanceof Long l) {
            return deserialize(l);
        }
        try {
            return deserialize((long) o);
        } catch (Exception e) {
            return null;
        }
    }

}