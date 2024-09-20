package com.github.oobila.bukkit.persistence.adapters.storage;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Getter
@Builder(toBuilder = true)
public class StoredData {

    private final String name;
    private final String data;
    private final long size;
    private final ZonedDateTime updatedDate;

}
