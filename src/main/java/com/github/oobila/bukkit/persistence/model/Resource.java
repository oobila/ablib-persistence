package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;

@Getter
public class Resource {

    private final ResourcePack resourcePack;
    private final String location;
    private final long size;
    @Getter
    @Setter
    private Object data;

    public Resource(ResourcePack resourcePack, String location, long size) {
        this.resourcePack = resourcePack;
        this.location = location;
        this.size = size;
    }

    public String getName() {
        return FilenameUtils.getBaseName(location);
    }

    public String getType() {
        return FilenameUtils.getExtension(location);
    }

    public void loadData(ResourcePack resourcePack) {
        resourcePack.getAdapter().loadData(this);
    }

    public void unloadData() {
        data = null;
    }

}
