package com.github.oobila.bukkit.persistence.adapters.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorldEditAdapterUtils {

//    public static void saveSchematic(File file, SchematicObject schematicObject) {
//        try (EditSession editSession = WorldEdit.getInstance().newEditSession(schematicObject.getRegion().getWorld())) {
//            editSession.setReorderMode(EditSession.ReorderMode.MULTI_STAGE);
//            ForwardExtentCopy copy = new ForwardExtentCopy(
//                    editSession,
//                    schematicObject.getRegion(),
//                    schematicObject,
//                    schematicObject.getMinimumPoint()
//            );
//            copy.setCopyingEntities(true);
//            copy.setRemovingEntities(true);
//            copy.setCopyingBiomes(false);
//            for (BlockVector2 bv2 : schematicObject.getRegion().getChunks()) {
//                Chunk chunk = BukkitAdapter.adapt(schematicObject.getRegion().getWorld()).getChunkAt(bv2.getX(), bv2.getZ());
//                chunk.load();
//            }
//            file.getParentFile().mkdirs();
//            try (
//                    FileOutputStream fos = new FileOutputStream(file);
//                    ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(fos)
//            ) {
//                Operations.complete(copy);
//                writer.write(schematicObject.getClipboard());
//            }
//        } catch (IOException | WorldEditException e) {
//            throw new RuntimeException(e);
//        }
//    }

}
