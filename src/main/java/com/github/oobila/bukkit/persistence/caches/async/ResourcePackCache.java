package com.github.oobila.bukkit.persistence.caches.async;

import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.code.ResourcePackCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.ResourcePackVehicle;
import com.github.oobila.bukkit.persistence.model.ResourcePack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ResourcePackCache<K> extends AsyncReadAndWriteCache<K, ResourcePack> {

    public ResourcePackCache(Plugin plugin, String name, Class<K> keyType, Map<Pattern, CodeAdapter<?>> codeAdapterMap) {
        super(
                plugin,
                name,
                new ResourcePackVehicle<>(keyType, new ResourcePackCodeAdapter(codeAdapterMap))
        );
    }
}
