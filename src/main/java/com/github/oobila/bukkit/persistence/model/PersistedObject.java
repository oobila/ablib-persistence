package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.time.ZonedDateTime;

@Getter
public abstract class PersistedObject implements ConfigurationSerializable {

    private final ZonedDateTime createdDate;

    protected PersistedObject() {
        this(ZonedDateTime.now());
    }

    protected PersistedObject(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
