package com.github.oobila.bukkit.persistence.caches.real;

import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.ResourcePackVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncOnDemandCache;
import com.github.oobila.bukkit.persistence.model.ResourcePack;

import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ResourcePackCache<K> extends AsyncOnDemandCache<K, ResourcePack> {

    public ResourcePackCache(String name, Class<K> keyType, Map<Pattern, CodeAdapter<?>> codeAdapterMap) {
        super(name, new ResourcePackVehicle<>(keyType, codeAdapterMap));
    }
}
