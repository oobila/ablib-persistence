package com.github.oobila.bukkit.persistence;

import com.github.oobila.bukkit.persistence.caches.AsyncPlayerCache;
import com.github.oobila.bukkit.persistence.caches.ICache;
import com.github.oobila.bukkit.persistence.caches.PlayerCache;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CacheManager {

    private static final List<ICache> caches = new ArrayList<>();
    @Getter
    private static final List<ICache> playerCaches = new ArrayList<>();

    public static void addCache(ICache cache) {
        if (cache instanceof PlayerCache<?,?> || cache instanceof AsyncPlayerCache<?,?>) {
            playerCaches.add(cache);
        }
        caches.add(cache);
    }

}
