package com.github.oobila.bukkit.persistence.serializers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer implements Serializer<LocalDate> {

    @Override
    public String serialize(LocalDate localDate) {
        return DateTimeFormatter.ISO_DATE.format(localDate);
    }

    @Override
    public LocalDate deserialize(String string) {
        return LocalDate.from(DateTimeFormatter.ISO_DATE.parse(string));
    }
}
