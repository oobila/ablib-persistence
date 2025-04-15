package com.github.oobila.bukkit.persistence.caches.real.model;

import com.github.oobila.bukkit.persistence.serializers.Serialization;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class MessageItem implements ConfigurationSerializable {

    private final String message;
    private final ZonedDateTime dateTime;

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("dateTime", Serialization.serialize(dateTime));
        return map;
    }

    public static MessageItem deserialize(Map<String, Object> args) {
        return new MessageItem(
                (String) args.get("message"),
                Serialization.deserialize(ZonedDateTime.class, (String) args.get("dateTime"))
        );
    }

}
