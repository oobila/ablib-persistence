package com.github.oobila.bukkit.persistence;

import com.github.oobila.bukkit.persistence.caches.PlayerReadCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheManager {

    @Getter
    private static final List<PlayerReadCache<?, ?>> playerReadCaches = new ArrayList<>();

    public static void register(PlayerReadCache<?, ?> cache) {
        playerReadCaches.add(cache);
    }

}
