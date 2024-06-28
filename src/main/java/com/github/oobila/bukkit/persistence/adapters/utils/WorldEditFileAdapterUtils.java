package com.github.oobila.bukkit.persistence.adapters.utils;

import com.github.oobila.bukkit.persistence.model.SchematicObject;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector2;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Chunk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorldEditFileAdapterUtils {

    public static SchematicObject loadSchematic(File saveFile) {
        try (FileInputStream fis = new FileInputStream(saveFile)) {
            return loadSchematic(fis, ZonedDateTime.ofInstant(Instant.ofEpochSecond(saveFile.lastModified()), UTC));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SchematicObject loadSchematic(InputStream inputStream, ZonedDateTime zonedDateTime) {
        try (ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(inputStream)) {
            Clipboard clipboard = reader.read();
            if (zonedDateTime == null) {
                zonedDateTime = ZonedDateTime.now();
            }
            return new SchematicObject(clipboard, zonedDateTime);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveSchematic(File file, SchematicObject schematicObject) {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(schematicObject.getRegion().getWorld())) {
            editSession.setReorderMode(EditSession.ReorderMode.MULTI_STAGE);
            ForwardExtentCopy copy = new ForwardExtentCopy(
                    editSession,
                    schematicObject.getRegion(),
                    schematicObject,
                    schematicObject.getMinimumPoint()
            );
            copy.setCopyingEntities(true);
            copy.setRemovingEntities(true);
            copy.setCopyingBiomes(false);
            for (BlockVector2 bv2 : schematicObject.getRegion().getChunks()) {
                Chunk chunk = BukkitAdapter.adapt(schematicObject.getRegion().getWorld()).getChunkAt(bv2.getX(), bv2.getZ());
                chunk.load();
            }
            file.getParentFile().mkdirs();
            try (
                    FileOutputStream fos = new FileOutputStream(file);
                    ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(fos)
            ) {
                Operations.complete(copy);
                writer.write(schematicObject.getClipboard());
            }
        } catch (IOException | WorldEditException e) {
            throw new RuntimeException(e);
        }
    }

}
