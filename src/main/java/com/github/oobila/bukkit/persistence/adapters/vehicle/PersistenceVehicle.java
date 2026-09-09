package com.github.oobila.bukkit.persistence.adapters.vehicle;

import com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter;
import com.github.oobila.bukkit.persistence.adapters.storage.StorageAdapter;
import com.github.oobila.bukkit.persistence.caches.Cache;
import com.github.oobila.bukkit.persistence.model.BackwardsCompatibility;
import com.github.oobila.bukkit.persistence.model.CacheItem;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Bridges a {@link Cache} to physical storage. A vehicle knows how to translate between
 * {@code CacheItem}s (the in-memory representation a {@code Cache} works with) and
 * {@link com.github.oobila.bukkit.persistence.adapters.storage.StoredData} (the raw, already
 * text-encoded representation a {@link StorageAdapter} reads/writes), using a {@link CodeAdapter}
 * to do the value (de)serialization in between.
 * <p>
 * The single implementation in active use, {@link DynamicVehicle}, is configured with a path
 * template describing whether/how data is partitioned per player and per record; not every
 * operation below is meaningful for every template (e.g. {@code save(Plugin, Map)} is unsupported
 * for a per-partition template) — such calls throw {@code PersistenceRuntimeException}.
 *
 * @param <K> the key type identifying individual records
 * @param <V> the value type being stored
 * @param <C> the {@code CacheItem} subtype wrapping each stored value plus its metadata
 */
@SuppressWarnings("unused")
public interface PersistenceVehicle<K, V, C extends CacheItem<K, V>> {

    /** Sets the plugin this vehicle is currently operating for (its data folder, resources, etc.). */
    void setPlugin(Plugin plugin);

    /** The cache this vehicle belongs to, used by {@link com.github.oobila.bukkit.persistence.model.OnDemandCacheItem} to lazily reload data. */
    Cache getCache();

    /** The path/table template this vehicle was configured with. */
    String getPathString();

    void setCache(Cache cache);

    /** Loads all global (unpartitioned) data this vehicle's template applies to. */
    Map<K, C> load(Plugin plugin);

    /** Loads all data belonging to a single partition. */
    Map<K, C> load(Plugin plugin, UUID partition);

    /** Loads a single record by partition and key, or {@code null} if it doesn't exist. */
    C load(Plugin plugin, UUID partition, K key);

    /** Lists the keys of every globally-stored record this vehicle's template applies to. */
    Collection<K> keys();

    /** Lists the keys of every record stored under a single partition. */
    Collection<K> keys(UUID partition);

    /** Copies the plugin's bundled default resource for this path into storage if nothing is there yet. */
    void copyDefaults();

    /** Overwrites all global data with the given map (unsupported for partitioned/on-demand templates). */
    void save(Plugin plugin, Map<K, C> map);

    /** Overwrites a single partition's data with the given map (unsupported for unpartitioned/on-demand templates). */
    void save(Plugin plugin, UUID partition, Map<K, C> map);

    /** Saves a single record under the given partition and key. */
    void save(Plugin plugin, UUID partition, K key, C cacheItem);

    /** Deletes all data belonging to a partition. */
    void delete(Plugin plugin, UUID partition);

    /** Deletes a single record. */
    void delete(Plugin plugin, UUID partition, K key);

    /** The adapter used to read/write this vehicle's serialized data to its storage medium. */
    StorageAdapter getStorageAdapter();

    /** The adapter used to (de)serialize this vehicle's value objects to/from text. */
    CodeAdapter<V> getCodeAdapter();

    Class<K> getKeyType();

    /** Registers a find/replace rule applied to data as it's read, to support renaming serialized classes/fields. */
    void addBackwardsCompatibility(BackwardsCompatibility backwardsCompatibility);

    List<BackwardsCompatibility> getBackwardsCompatibilityList();

}