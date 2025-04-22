package com.github.oobila.bukkit.persistence.serializers;

import org.bukkit.Location;

public class LocationSerializer implements KeySerializer<Location> {

    WorldSerializer worldSerializer = new WorldSerializer();

    @Override
    public String serialize(Location location) {
        return String.format(
                "%s_%s_%s_%s",
                worldSerializer.serialize(location.getWorld()),
                parse(location.getX()),
                parse(location.getY()),
                parse(location.getZ())
        );
    }

    @Override
    public Location deserialize(String string) {
        String[] split = string.split("_");
        return new Location(
                split[0].isEmpty() ? null : worldSerializer.deserialize(split[0]),
                parse(split[1]),
                parse(split[2]),
                parse(split[3])
        );
    }

    private String parse(double d) {
        return Double.toString(d).replace(".","-");
    }

    private double parse(String s) {
        return Double.parseDouble(s.replace("-", "."));
    }
}
