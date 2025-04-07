package com.github.oobila.bukkit.persistence.serializers;

public class IntSerializer implements KeySerializer<Integer> {

    @Override
    public String serialize(Integer object) {
        return object.toString();
    }

    @Override
    public Integer deserialize(String string) {
        return Integer.parseInt(string);
    }
}
