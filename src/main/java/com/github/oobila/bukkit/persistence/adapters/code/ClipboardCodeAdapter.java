package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

@SuppressWarnings("unused")
@RequiredArgsConstructor
@Getter
public class ClipboardCodeAdapter implements CodeAdapter<Clipboard> {

    @Setter
    private Plugin plugin;

    @Override
    public Class<Clipboard> getType() {
        return Clipboard.class;
    }

    @Override
    public Map<String, Clipboard> toObjects(StoredData storedData) {
        try (
                ByteArrayInputStream inputStream = new ByteArrayInputStream(storedData.getData().getBytes(StandardCharsets.ISO_8859_1));
                ClipboardReader clipboardReader = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getReader(inputStream)
        ) {
            return Map.of(Strings.EMPTY, clipboardReader.read());
        } catch (IOException e) {
            log(Level.SEVERE, "Could not load clipboard.");
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public String fromObjects(Map<String, Clipboard> map) {
        Clipboard clipboard = map.values().iterator().next();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(8192)) {
            try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getWriter(outputStream)) {
                clipboardWriter.write(clipboard);
            }
            return outputStream.toString(StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            log(Level.SEVERE, "Could not save clipboard.");
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }
}