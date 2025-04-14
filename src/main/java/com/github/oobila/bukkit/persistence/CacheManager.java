package com.github.oobila.bukkit.persistence;

import com.github.oobila.bukkit.persistence.caches.ReadCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
public class CacheManager {

    @Getter
    private static final List<ReadCache<?, ?, ?>> playerReadCaches = new ArrayList<>();

    public static void registerPlayerCache(ReadCache<?, ?, ?> cache) {
        playerReadCaches.add(cache);
    }

}
