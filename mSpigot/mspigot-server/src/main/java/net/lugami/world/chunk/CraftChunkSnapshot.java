package net.lugami.world.chunk;

import net.minecraft.server.NBTTagCompound;
import net.lugami.ChunkSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CraftChunkSnapshot implements ChunkSnapshot {

    private final ChunkSectionSnapshot[] sections = new ChunkSectionSnapshot[16];
    private final List<NBTTagCompound> tileEntities = new ArrayList<>();

    public ChunkSectionSnapshot[] getSections() {
        return this.sections;
    }

    public List<NBTTagCompound> getTileEntities() {
        return this.tileEntities;
    }
}
