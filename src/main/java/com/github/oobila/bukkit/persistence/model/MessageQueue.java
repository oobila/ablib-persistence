package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class MessageQueue implements Collection<String>, ConfigurationSerializable {

    @Delegate
    private final List<String> messages;

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("messages", messages);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static MessageQueue deserialize(Map<String, Object> args) {
        return new MessageQueue((List<String>) args.get("messages"));
    }
}
