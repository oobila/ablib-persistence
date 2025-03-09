package com.github.oobila.bukkit.persistence.old.caches.real;

import com.github.oobila.bukkit.persistence.old.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.old.vehicles.ResourcePackVehicle;
import com.github.oobila.bukkit.persistence.old.caches.async.AsyncOnDemandCache;
import com.github.oobila.bukkit.persistence.old.model.ResourcePack;

import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ResourcePackCache<K> extends AsyncOnDemandCache<K, ResourcePack> {

    public ResourcePackCache(String name, Class<K> keyType, Map<Pattern, CodeAdapter<?>> codeAdapterMap) {
        super(name, new ResourcePackVehicle<>(keyType, codeAdapterMap));
    }
}
