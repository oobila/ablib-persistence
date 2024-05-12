package com.github.oobila.bukkit.persistence.model;

public enum ResourceType {

    YAML("yml"),
    SCHEMATIC("schem");

    private final String fileExtension;

    ResourceType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public static ResourceType of(String string) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.fileExtension.equalsIgnoreCase(string)) {
                return resourceType;
            }
        }
        return null;
    }
}
