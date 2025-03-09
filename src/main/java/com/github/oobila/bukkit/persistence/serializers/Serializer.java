package com.github.oobila.bukkit.persistence.serializers;

public interface Serializer<T> {

    String serialize(T object);

    T deserialize(String string);

}
