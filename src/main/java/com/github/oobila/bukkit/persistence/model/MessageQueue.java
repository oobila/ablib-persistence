package com.github.oobila.bukkit.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class MessageQueue extends PersistedObject implements Collection<String> {

    @Delegate
    private final List<String> messages;

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("", messages);
        return map;
    }

    public static MessageQueue deserialize(Map<String, Object> args) {
        return new MessageQueue((List<String>) args.get(""));
    }
}
