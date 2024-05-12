package com.github.oobila.bukkit.persistence.model;

import java.util.HashMap;
import java.util.Map;

public class Resource extends PersistedObject{
    
    private final Map<String, Map<?, ?>> children = new HashMap<>();
    
    @Override
    public Map<String, Object> serialize() {
        return null;
    }
    
    public static Resource deserialize(Map<String, Object> args) {
        return null;
    }
}
