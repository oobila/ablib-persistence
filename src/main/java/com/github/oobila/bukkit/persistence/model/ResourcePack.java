package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.HashMap;
import java.util.Map;

/**
 * A named bundle of heterogeneous {@link Resource}s (each potentially a different type) stored
 * and retrieved together as a single zip archive — see
 * {@link com.github.oobila.bukkit.persistence.adapters.code.ResourcePackCodeAdapter}.
 */
@RequiredArgsConstructor
@Getter
public class ResourcePack implements Map<String, Resource<?>>{

    @Delegate
    private final Map<String, Resource<?>> resources = new HashMap<>();

    private final String name;

}
