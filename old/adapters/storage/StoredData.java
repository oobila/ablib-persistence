package com.github.oobila.bukkit.persistence.old.adapters.storage;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class StoredData {

    private final String name;
    private final String data;
    private final long size;
    private final ZonedDateTime updatedDate;
    @Setter
    private UUID ownerId;

    public StoredData(String name, String data, long size, ZonedDateTime updatedDate) {
        this.name = name;
        this.data = data;
        this.size = size;
        this.updatedDate = updatedDate;
        this.ownerId = null;
    }

}
