package com.github.oobila.bukkit.persistence.codeadapter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Getter(AccessLevel.PACKAGE)
public abstract class MultiCodeAdapter<K, V> implements CodeAdapter<Map<K, V>> {

    private final Class<K> keyType;

}
