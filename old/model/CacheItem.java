package com.github.oobila.bukkit.persistence.old.model;

import com.github.oobila.bukkit.persistence.old.adapters.storage.StoredData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
public class CacheItem<K, D> {

    private final K key;
    private final Class<D> type;
    protected D data;
    private final long size;
    protected final ZonedDateTime updatedDate;
    private final OfflinePlayer owner;

    public CacheItem(Class<D> type, K key, D data, StoredData storedData) {
        this(type, key, data, storedData.getSize(), storedData.getUpdatedDate(), storedData.getOwnerId());
    }

    public CacheItem(Class<D> type, K key, D data, long size, ZonedDateTime updatedDate) {
        this.type = type;
        this.key = key;
        this.data = data;
        this.size = size;
        this.updatedDate = updatedDate;
        this.owner = null;
    }

    public CacheItem(Class<D> type, K key, D data, long size, ZonedDateTime updatedDate, UUID ownerId) {
        this.type = type;
        this.key = key;
        this.data = data;
        this.size = size;
        this.updatedDate = updatedDate;
        this.owner = Bukkit.getOfflinePlayer(ownerId);
    }
}

