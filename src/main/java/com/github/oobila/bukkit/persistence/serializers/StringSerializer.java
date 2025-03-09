package com.github.oobila.bukkit.persistence.serializers;

public class StringSerializer implements Serializer<String> {

    @Override
    public String serialize(String object) {
        return object;
    }

    @Override
    public String deserialize(String string) {
        return string;
    }
}
