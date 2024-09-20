package com.github.oobila.bukkit.persistence.serializers;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeSerializer implements KeySerializer<LocalTime> {

    @Override
    public String serialize(LocalTime localTime) {
        return DateTimeFormatter.ISO_TIME.format(localTime);
    }

    @Override
    public LocalTime deserialize(String string) {
        return LocalTime.from(DateTimeFormatter.ISO_TIME.parse(string));
    }
}
