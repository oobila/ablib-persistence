package com.github.oobila.bukkit.persistence.adapters.code;

import com.github.oobila.bukkit.persistence.PersistenceRuntimeException;
import com.github.oobila.bukkit.persistence.adapters.storage.StoredData;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import static com.github.oobila.bukkit.common.ABCommon.log;

@SuppressWarnings("unused")
public class ClipboardCodeAdapter implements CodeAdapter<Clipboard> {

    @Override
    public Class<Clipboard> getType() {
        return Clipboard.class;
    }

    @Override
    public Clipboard toObject(StoredData storedData) {
        try (
                ByteArrayInputStream inputStream = new ByteArrayInputStream(storedData.getData().getBytes());
                ClipboardReader clipboardReader = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getReader(inputStream)
        ) {
            return clipboardReader.read();
        } catch (IOException e) {
            log(Level.SEVERE, "Could not load clipboard.");
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }

    @Override
    public String fromObject(Clipboard clipboard) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(8192); FileOutputStream fos = new FileOutputStream("D:\\Minecraft\\Server\\plugins\\blaa.schem")) {
            try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getWriter(outputStream)) {
                clipboardWriter.write(clipboard);
            }
            try (ClipboardWriter clipboardWriter = BuiltInClipboardFormat.SPONGE_V3_SCHEMATIC.getWriter(fos)) {
                clipboardWriter.write(clipboard);
            }
            return outputStream.toString();
        } catch (IOException e) {
            log(Level.SEVERE, "Could not save clipboard.");
            log(Level.SEVERE, e);
            throw new PersistenceRuntimeException(e);
        }
    }
}
