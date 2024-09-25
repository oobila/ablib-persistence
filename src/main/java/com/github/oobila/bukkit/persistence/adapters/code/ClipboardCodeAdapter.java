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
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
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
    public Clipboard toObject(StoredData storedData) {
        try {
            File file = new File(plugin.getDataFolder(), "temp/schematic.schem");
            Files.writeString(file.toPath(), storedData.getData());
            try (FileInputStream fis = new FileInputStream(file);
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
        File file = new File(plugin.getDataFolder(), "temp/schematic.schem");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getWriter(fos)) {
                clipboardWriter.write(clipboard);
            }
            return Files.readString(file.toPath());
        } catch (IOException e) {
            log(Level.SEVERE, "Could not save clipboard.");
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }
}
