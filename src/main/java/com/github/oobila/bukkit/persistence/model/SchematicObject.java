package com.github.oobila.bukkit.persistence.model;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
public class SchematicObject extends PersistedObject implements Clipboard {

    private static final Map<String, Object> EMPTY_MAP = new HashMap<>();

    @Delegate(types = Clipboard.class)
    private final Clipboard clipboard;

    public SchematicObject(Clipboard clipboard) {
        this(clipboard, ZonedDateTime.now());
    }

    public SchematicObject(Clipboard clipboard, ZonedDateTime createdDate) {
        super(createdDate);
        this.clipboard = clipboard;
    }

    @Override
    public Map<String, Object> serialize() {
        return EMPTY_MAP; //not called
    }
}
