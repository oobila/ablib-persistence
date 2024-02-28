package com.github.oobila.bukkit.persistence.serializers;

public class StringSerializer implements KeySerializer<String> {

    @Override
    public String serialize(String object) {
        return object;
    }

    @Override
    public String deserialize(String string) {
        return string;
    }
}
