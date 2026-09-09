package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.alastairbooth.placeholderpattern.PlaceholderPattern;
import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.code.ResourcePackCodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.FileStorageAdapter;
import com.github.oobila.bukkit.persistence.model.OnDemandCacheItem;
import com.github.oobila.bukkit.persistence.model.ResourcePack;

import java.util.Map;

/**
 * A {@link DynamicVehicle} preconfigured to load {@link ResourcePack}s — zip archives bundling
 * several typed resources — from a directory of {@code .zip} files, one resource pack per file,
 * keyed by file name. Resource packs are treated as global data, not partitioned per player: the
 * path template this vehicle builds only contains {@link DynamicVehicle#KEY_STRING}, never
 * {@link DynamicVehicle#PARTITION_STRING}.
 * <p>
 * Loading is on-demand: {@code load(Plugin)} only reads each zip's metadata (size/last-modified),
 * wrapping it in an {@link OnDemandCacheItem} that lazily reads and unpacks the full archive the
 * first time it's actually accessed, rather than eagerly unzipping every resource pack up front —
 * see {@link com.github.oobila.bukkit.persistence.caches.real.ResourcePackCache}, the ready-to-use
 * cache built on this vehicle. Use
 * {@link com.github.oobila.bukkit.persistence.caches.real.SimpleAsyncResourceCache} instead when
 * the full set of resource packs is small enough to eagerly hold in memory.
 *
 * @param <K> the key type identifying individual resource packs (their file name, minus extension)
 */
public class ResourcePackVehicle<K> extends DynamicVehicle<K, ResourcePack, OnDemandCacheItem<K, ResourcePack>> {

    private static final String EXTENSION = "zip";

    /**
     * @param directory      the directory (relative to the plugin's data folder) resource pack
     *                        zip files are stored under, e.g. {@code "resourcepacks"}
     * @param keyType        the runtime type of {@code K}, used to (de)serialize each pack's key
     *                        (its file name) via {@link com.github.oobila.bukkit.persistence.serializers.Serialization}
     * @param codeAdapterMap maps each resource type's {@link CodeAdapter} to a
     *                        {@link PlaceholderPattern} that matches that type's entries by file
     *                        name within a pack's zip archive — see {@link ResourcePackCodeAdapter}
     */
    public ResourcePackVehicle(String directory, Class<K> keyType, Map<PlaceholderPattern, CodeAdapter<?>> codeAdapterMap) {
        super(
                trimTrailingSlash(directory) + "/" + KEY_STRING + "." + EXTENSION,
                true,
                keyType,
                new FileStorageAdapter(),
                new ResourcePackCodeAdapter(codeAdapterMap)
        );
    }

    private static String trimTrailingSlash(String directory) {
        return directory.endsWith("/") ? directory.substring(0, directory.length() - 1) : directory;
    }

}
