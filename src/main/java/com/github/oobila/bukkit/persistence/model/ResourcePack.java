package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ResourcePack {

    private final File file;
    private final String name;
    private final long size;
    private final ZonedDateTime createdDate;
    private final Map<String, Resource> resources = new HashMap<>();

    public ResourcePack(File file) throws IOException {
        this.file = file;
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        name = FilenameUtils.getBaseName(file.getName());
        size = attr.size();
        createdDate = attr.creationTime().toInstant().atZone(ZoneOffset.UTC);
    }
}