package com.github.oobila.bukkit.persistence.serializers;

/**
 * Converts a cache <em>key</em> of type {@code T} to and from a short string suitable for use in
 * a file name, path segment, or SQL column — as opposed to a
 * {@link com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter}, which (de)serializes
 * the stored <em>value</em>. Register an implementation for a new key type via
 * {@link Serialization#register}.
 *
 * @param <T> the key type this serializer handles
 */
public interface KeySerializer<T extends Object> {

    String serialize(T object);

    T deserialize(String string);

}
