package com.github.oobila.bukkit.persistence.serializers;

/** No-op {@link KeySerializer} for {@code String} keys — used as-is. */
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
