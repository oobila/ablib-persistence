package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import lombok.RequiredArgsConstructor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

@SuppressWarnings("unused")
@RequiredArgsConstructor
public class ClipboardCodeAdapter implements CodeAdapter<Clipboard> {

    private final String tempDir;

    @Override
    public Class<Clipboard> getType() {
        return Clipboard.class;
    }

    @Override
    public Clipboard toObject(StoredData storedData) {
        try {
            Files.writeString(Path.of(tempDir), storedData.getData());
            try (FileInputStream fis = new FileInputStream(tempDir);
                 ClipboardReader clipboardReader = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getReader(fis)) {
                return clipboardReader.read();
            }
        } catch (IOException e) {
            log(Level.SEVERE, "Could not load clipboard.");
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public String fromObject(Clipboard clipboard) {
        try (FileOutputStream fos = new FileOutputStream(tempDir)) {
            try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getWriter(fos)) {
                clipboardWriter.write(clipboard);
            }
            return Files.readString(Path.of(tempDir));
        } catch (IOException e) {
            log(Level.SEVERE, "Could not save clipboard.");
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }
}
