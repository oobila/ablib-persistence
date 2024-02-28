package com.github.oobila.bukkit.persistence.serializers;

public interface KeySerializer<T extends Object> {

    String serialize(T object);

    T deserialize(String string);

}
