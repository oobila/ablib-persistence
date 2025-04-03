package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.plugin.Plugin;

import java.util.Map;

@Getter
public class StringCodeAdapter implements CodeAdapter<String> {

    @Setter
    private Plugin plugin;

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public Map<String, String> toObjects(StoredData storedData) {
        return Map.of(Strings.EMPTY, storedData.getData());
    }

    @Override
    public String fromObjects(Map<String, String> map) {
        return map.values().iterator().next();
    }
}
