package com.github.oobila.bukkit.persistence.caches.real;

import com.github.alastairbooth.placeholderpattern.PlaceholderPattern;
import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.code.ResourcePackCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncReadAndWriteCache;
import com.github.oobila.bukkit.persistence.model.ResourcePack;

import java.util.Map;

@SuppressWarnings("unused")
public class SimpleAsyncResourceCache<K> extends AsyncReadAndWriteCache<K, ResourcePack> {

    public SimpleAsyncResourceCache(String pathString, Class<K> keyType, Map<PlaceholderPattern, CodeAdapter<?>> codeAdapterMap) {
        super(
                new DynamicVehicle<>(
                        pathString,
                        false,
                        keyType,
                        new FileStorageAdapter(),
                        new ResourcePackCodeAdapter(codeAdapterMap)
                )
        );
    }

}
