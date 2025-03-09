package com.github.oobila.bukkit.persistence.codeadapter.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Getter
public class StoredData {

    private final String path;
    private final String data;
    private final long size;
    private final ZonedDateTime updatedDate;

    public StoredData(String data) {
        this.path = null;
        this.data = data;
        this.size = -1L;
        this.updatedDate = null;
    }
}
