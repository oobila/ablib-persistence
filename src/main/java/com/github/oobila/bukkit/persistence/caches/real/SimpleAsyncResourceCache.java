package com.github.oobila.bukkit.persistence.caches.real;

import com.github.alastairbooth.placeholderpattern.PlaceholderPattern;
import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.code.ResourcePackCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncReadAndWriteCache;
import com.github.oobila.bukkit.persistence.model.ResourcePack;

import java.util.Map;

/**
 * An asynchronous, file-backed cache of {@link ResourcePack}s — zipped bundles of several
 * differently-typed resources (each stored as its own zip entry, matched to a
 * {@link CodeAdapter} by filename pattern via {@code codeAdapterMap}), read/written as a whole
 * through {@code ResourcePackCodeAdapter}.
 *
 * @param <K> the key type identifying individual resource packs
 */
@SuppressWarnings("unused")
public class SimpleAsyncResourceCache<K> extends AsyncReadAndWriteCache<K, ResourcePack> {

    /**
     * @param codeAdapterMap maps a {@link PlaceholderPattern} (matching a zip entry's file name,
     *                        with exactly one placeholder) to the {@link CodeAdapter} used to
     *                        (de)serialize entries whose name matches that pattern
     */
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
