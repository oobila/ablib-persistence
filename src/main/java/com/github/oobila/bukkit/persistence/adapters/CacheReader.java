package com.github.oobila.bukkit.persistence.adapters;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

public interface CacheReader {

    default void addDeserializeReplacerRule(String oldString, String newString) {
        CacheReaderHelper.deserializeReplacementRules.put(oldString, newString);
    }

    default Map<String, String> getDeserializeReplacementRules() {
        return CacheReaderHelper.deserializeReplacementRules;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    class CacheReaderHelper {
        static final Map<String, String> deserializeReplacementRules = new HashMap<>();
    }

}
