package com.github.oobila.bukkit.persistence.adapters.storage;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import static com.github.oobila.bukkit.common.ABCommon.log;

public class ConfigStorageAdapter extends FileStorageAdapter {

    private static final Pattern CONFIG_PATTERN = Pattern.compile("(?<config>[a-z0-9]+[a-z0-9-]*[a-z0-9]+):.*");

    @Override
    public List<StoredData> read(Plugin plugin, String fileName) {
        StoredData storedData = super.read(plugin, fileName).get(0);
        List<String> defaultConfigs = readDefaults(plugin, fileName);
        String newData = String.join("\n", enrichDefaults(Arrays.asList(storedData.getData().split("\n")), defaultConfigs));
        return Collections.singletonList(storedData.toBuilder()
                .data(newData)
                .build());
    }

    private List<String> enrichDefaults(List<String> config, List<String> defaults) {
        outer: for (String d : defaults) {
            String defaultConfigName = CONFIG_PATTERN.matcher(d).group("config");
            for (String configItem : config) {
                String configItemName = CONFIG_PATTERN.matcher(configItem).group("config");
                if (defaultConfigName.equals(configItemName)) {
                    continue outer;
                }
            }
            config.add(d);
        }
        return config;
    }

    public List<String> readDefaults(Plugin plugin, String fileName) {
        Path path = getPath(plugin, fileName);
        sneakyForceMkdir(path.getParent().toFile());
        try (InputStream inputStream = plugin.getResource(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            return bufferedReader.lines().filter(string -> CONFIG_PATTERN.matcher(string).matches()).toList();
        } catch (IOException e) {
            log(Level.SEVERE, "Could not read defaults for: {0}", fileName);
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

}
