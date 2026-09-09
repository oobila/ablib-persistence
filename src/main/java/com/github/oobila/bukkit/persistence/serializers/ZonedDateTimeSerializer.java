package com.github.oobila.bukkit.persistence.serializers;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/** {@link KeySerializer} for {@code ZonedDateTime} keys, using ISO-8601 date-time format. */
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
