package com.github.oobila.bukkit.persistence.serializers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** {@link KeySerializer} for {@code LocalDate} keys, using ISO-8601 date format. */
public class LocalDateSerializer implements KeySerializer<LocalDate> {

    @Override
    public String serialize(LocalDate localDate) {
        return DateTimeFormatter.ISO_DATE.format(localDate);
    }

    @Override
    public LocalDate deserialize(String string) {
        return LocalDate.from(DateTimeFormatter.ISO_DATE.parse(string));
    }
}
