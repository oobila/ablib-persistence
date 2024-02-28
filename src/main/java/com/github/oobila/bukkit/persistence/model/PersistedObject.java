package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.time.ZonedDateTime;

public abstract class PersistedObject implements ConfigurationSerializable {

    @Getter
    private final ZonedDateTime createdDate;

    public PersistedObject() {
        this(ZonedDateTime.now());
    }

    public PersistedObject(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
