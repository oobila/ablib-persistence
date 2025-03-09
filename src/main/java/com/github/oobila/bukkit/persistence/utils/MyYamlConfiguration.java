package com.github.oobila.bukkit.persistence.utils;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Map;

/**
 * An implementation of {@link Configuration} which saves all files in Yaml.
 * Note that this implementation is not synchronized.
 */
public class MyYamlConfiguration extends FileConfiguration {
    protected static final String BLANK_CONFIG = "{}\n";
    private static final String HEADER = """
            ################################################################
            # This is a data file created by your plugin (not a config)
            # Do not edit unless you know what you are doing
            ################################################################
            """;
    private final DumperOptions dumperOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter(dumperOptions);

    @NotNull
    @Override
    public String saveToString() {
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(new YamlConstructor(new LoaderOptions()), yamlRepresenter, dumperOptions);

        String dump = yaml.dump(getValues(false));

        if (dump.equals(BLANK_CONFIG)) {
            dump = "";
        }

        return HEADER + dump;
    }

    @Override
    public void loadFromString(@NotNull String contents) throws InvalidConfigurationException {
        Map<?, ?> input;
        try {
            Yaml yaml = new Yaml(new YamlConstructor(new LoaderOptions()), yamlRepresenter, dumperOptions);
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

}