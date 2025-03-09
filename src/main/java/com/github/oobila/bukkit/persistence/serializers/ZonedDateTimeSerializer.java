package com.github.oobila.bukkit.persistence.serializers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ZonedDateTimeSerializer implements KeySerializer<ZonedDateTime> {

    @Override
    public String serialize(ZonedDateTime zonedDateTime) {
        return DateTimeFormatter.ISO_DATE_TIME.format(zonedDateTime);
    }

    @Override
    public ZonedDateTime deserialize(String string) {
        return ZonedDateTime.from(DateTimeFormatter.ISO_DATE_TIME.parse(string));
    }
}
