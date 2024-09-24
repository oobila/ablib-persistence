package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class ResourcePack implements Map<String, Resource<?>>{

    @Delegate
    private final Map<String, Resource<?>> resources = new HashMap<>();

    private final String name;

}
