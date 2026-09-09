package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@code Map} of {@link CacheItem}s tagged with the storage path/name ({@link #parentKey}) they
 * should all be written to as one combined record — used by
 * {@link com.github.oobila.bukkit.persistence.adapters.vehicle.DynamicVehicle} when saving a
 * whole partition/global map at once via a single {@link com.github.oobila.bukkit.persistence.adapters.code.CodeAdapter}
 * call (as opposed to one file per key).
 */
@RequiredArgsConstructor
@Getter
public class CacheItems<K, V, C extends CacheItem<K, V>> implements Map<K, C> {

    /** The resolved storage path/name all items in this map are (or will be) stored together under. */
    private final String parentKey;

    @Delegate
    private final Map<K, C> map = new HashMap<>();

}
