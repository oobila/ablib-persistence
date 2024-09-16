package com.github.oobila.bukkit.persistence.model;

public class Resource {

    private final String name;
    private final long size;
    private Object data;

    public Resource(String name, long size) {
        this.name = name;
        this.size = size;
    }

    public void clearData() {
        data = null;
    }
}
