package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

@Getter
public class Resource {

    private final String location;
    private final long size;
    @Setter
    private Object data;

    public Resource(String location, long size) {
        this.location = location;
        this.size = size;
    }

    public String getName() {
        return FilenameUtils.getBaseName(location);
    }

    public String getType() {
        return FilenameUtils.getExtension(location);
    }

}
