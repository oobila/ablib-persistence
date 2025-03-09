package com.github.oobila.bukkit.persistence.old.adapters.code;

import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

@Getter
public class StringCodeAdapter implements CodeAdapter<String> {

    @Setter
    private Plugin plugin;

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public String toObject(StoredData storedData) {
        return storedData.getData();
    }

    @Override
    public String fromObject(String object) {
        return object;
    }
}
