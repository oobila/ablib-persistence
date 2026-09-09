package com.github.oobila.bukkit.persistence.serializers;

import com.github.alastairbooth.abid.ABID;

/** {@link KeySerializer} for {@code ABID} keys (ablib-common's identifier type). */
public class ABIDSerializer implements KeySerializer<ABID> {

    @Override
    public String serialize(ABID object) {
        return object.toString();
    }

    @Override
    public ABID deserialize(String string) {
        return ABID.fromString(string);
    }
}
