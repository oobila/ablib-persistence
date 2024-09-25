package com.github.oobila.bukkit.persistence.adapters.utils;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of {@link Configuration} which saves all files in Yaml.
 * Note that this implementation is not synchronized.
 */
public class MyYamlConfiguration extends FileConfiguration {
    protected static final String COMMENT_PREFIX = "# ";
    protected static final String BLANK_CONFIG = "{}\n";
    private final DumperOptions dumperOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter(dumperOptions);
    private final Yaml yaml = new Yaml(new YamlConstructor(new LoaderOptions()), yamlRepresenter, dumperOptions);

    @NotNull
    @Override
    public String saveToString() {
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        addClassTags(yamlRepresenter);

        String header = "";
        String dump = yaml.dump(getValues(false));

        if (dump.equals(BLANK_CONFIG)) {
            dump = "";
        }

        return header + dump;
    }

    @Override
    public void loadFromString(@NotNull String contents) throws InvalidConfigurationException {
        Map<?, ?> input;
        try {
            input = yaml.load(contents);
        } catch (YAMLException e) {
            throw new InvalidConfigurationException(e);
        } catch (ClassCastException e) {
            throw new InvalidConfigurationException("Top level is not a Map.");
        }

        if (input != null) {
            convertMapsToSections(input, this);
        }
    }

    protected void convertMapsToSections(Map<?, ?> input, ConfigurationSection section) {
        for (Map.Entry<?, ?> entry : input.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            if (value instanceof Map) {
                convertMapsToSections((Map<?, ?>) value, section.createSection(key));
            } else {
                section.set(key, value);
            }
        }
    }

    private void addClassTags(Representer representer) {
        Map<String, Object> valueMap = getValues(true);
        Set<Class<?>> types = new HashSet<>();
        valueMap.forEach((string, object) -> addClassTags(types, object.getClass()));
        types.forEach(type -> representer.addClassTag(type, Tag.MAP));
    }

    private void addClassTags(Set<Class<?>> types, Class<?> type) {
        if (types.contains(type) || !ConfigurationSerializable.class.isAssignableFrom(type)) {
            return;
        }
        types.add(type);
        for (Field field : type.getDeclaredFields()) {
            addClassTags(types, field.getType());
            if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
                classes(parameterizedType).forEach(aClass -> addClassTags(types, aClass));
            } else {
                addClassTags(types, field.getGenericType().getClass());
            }
        }
    }

    private Set<Class<?>> classes(ParameterizedType parameterizedType) {
        //bit of a hack, hope it works
        Set<Class<?>> classes = new HashSet<>();
        String[] nameSegments = parameterizedType.getTypeName().split("[<,>]");
        for (String nameSegment : nameSegments) {
            try {
                Class<?> c = Class.forName(nameSegment);
                classes.add(c);
            } catch (Exception e){
                //do nothing
            }
        }
        return classes;
    }

}