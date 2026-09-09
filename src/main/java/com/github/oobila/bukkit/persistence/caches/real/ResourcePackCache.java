package com.github.oobila.bukkit.persistence.caches.real;

import com.github.alastairbooth.placeholderpattern.PlaceholderPattern;
import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.vehicle.ResourcePackVehicle;
import com.github.oobila.bukkit.persistence.caches.async.AsyncOnDemandCache;
import com.github.oobila.bukkit.persistence.model.ResourcePack;

import java.util.Map;

/**
 * An on-demand cache of {@link ResourcePack}s — zip archives bundling several typed resources,
 * e.g. a set of item/schematic templates a plugin ships or generates — stored as one {@code .zip}
 * file per pack under a directory. Each pack's content is only read and unpacked when actually
 * accessed (and evicted again shortly after, per {@link AsyncOnDemandCache}), so this is a
 * reasonable choice even for a large number of resource packs that would be wasteful to hold
 * fully in memory at once.
 * <p>
 * Use {@link SimpleAsyncResourceCache} instead when the full set of resource packs is small
 * enough to eagerly load and keep resident in memory.
 *
 * @param <K> the key type identifying individual resource packs (their file name, minus extension)
 */
@SuppressWarnings("unused")
public class ResourcePackCache<K> extends AsyncOnDemandCache<K, ResourcePack> {

    /**
     * @param directory      the directory (relative to the plugin's data folder) resource pack
     *                        zip files are stored under, e.g. {@code "resourcepacks"}
     * @param codeAdapterMap maps each resource type's {@link CodeAdapter} to a
     *                        {@link PlaceholderPattern} that matches that type's entries by file
     *                        name within a pack's zip archive
     */
    public ResourcePackCache(String directory, Class<K> keyType, Map<PlaceholderPattern, CodeAdapter<?>> codeAdapterMap) {
        super(new ResourcePackVehicle<>(directory, keyType, codeAdapterMap));
    }

}
