package com.github.oobila.bukkit.persistence.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class IntegerMetric extends PersistedObject {

    private int value;

    public IntegerMetric() {
        this.value = 0;
    }

    public IntegerMetric(int value) {
        this.value = value;
    }

    public int incrementAndGet() {
        value++;
        return value;
    }

    public int incrementAndGet(int amount) {
        value += amount;
        return value;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("", value);
        return map;
    }

    public static IntegerMetric deserialize(Map<String, Object> args) {
        return new IntegerMetric((Integer) args.get(""));
    }

}
