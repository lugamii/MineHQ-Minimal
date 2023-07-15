package net.lugami.qlib.util;

import java.util.*;

import net.minecraft.server.v1_7_R4.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.*;
import org.bukkit.entity.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.*;
import net.minecraft.server.v1_7_R4.*;
import com.google.common.collect.*;

public class BlockUtils {

    private static final Set<Material> INTERACTABLE = ImmutableSet.of(Material.FENCE_GATE, Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.CHEST, Material.HOPPER, Material.DISPENSER, Material.WOODEN_DOOR, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.TRAPPED_CHEST, Material.TRAP_DOOR, Material.LEVER, Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.BED_BLOCK, Material.ANVIL, Material.BEACON);

    public static boolean isInteractable(Block block) {
        return isInteractable(block.getType());
    }

    public static boolean isInteractable(Material material) {
        return BlockUtils.INTERACTABLE.contains(material);
    }

    public static boolean setBlockFast(World world, int x, int y, int z, int blockId, byte data) {
        net.minecraft.server.v1_7_R4.World w = ((CraftWorld)world).getHandle();
        Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
        return a(chunk, x & 0xF, y, z & 0xF, net.minecraft.server.v1_7_R4.Block.getById(blockId), data);
    }

    private static void queueChunkForUpdate(Player player, int cx, int cz) {
        ((CraftPlayer)player).getHandle().chunkCoordIntPairQueue.add(new ChunkCoordIntPair(cx, cz));
    }

    private static boolean a(Chunk that, int i, int j, int k, net.minecraft.server.v1_7_R4.Block block, int l) {
        int i2 = k << 4 | i;
        if (j >= that.b[i2] - 1) {
            that.b[i2] = -999;
        }
        int j2 = that.heightMap[i2];
        net.minecraft.server.v1_7_R4.Block block2 = that.getType(i, j, k);
        int k2 = that.getData(i, j, k);
        if (block2 == block && k2 == l) {
            return false;
        }
        boolean flag = false;
        ChunkSection chunksection = that.getSections()[j >> 4];
        if (chunksection == null) {
            if (block == Blocks.AIR) {
                return false;
            }
            ChunkSection[] sections = that.getSections();
            int n = j >> 4;
            ChunkSection chunkSection = new ChunkSection(j >> 4 << 4, !that.world.worldProvider.g);
            sections[n] = chunkSection;
            chunksection = chunkSection;
            flag = (j >= j2);
        }
        int l2 = that.locX * 16 + i;
        int i3 = that.locZ * 16 + k;
        if (!that.world.isStatic) {
            block2.f(that.world, l2, j, i3, k2);
        }
        if (!(block2 instanceof IContainer)) {
            chunksection.setTypeId(i, j & 0xF, k, block);
        }
        if (!that.world.isStatic) {
            block2.remove(that.world, l2, j, i3, block2, k2);
        }
        else if (block2 instanceof IContainer && block2 != block) {
            that.world.p(l2, j, i3);
        }
        if (block2 instanceof IContainer) {
            chunksection.setTypeId(i, j & 0xF, k, block);
        }
        if (chunksection.getTypeId(i, j & 0xF, k) != block) {
            return false;
        }
        chunksection.setData(i, j & 0xF, k, l);
        if (flag) {
            that.initLighting();
        }
        if (block2 instanceof IContainer) {
            TileEntity tileentity = that.e(i, j, k);
            if (tileentity != null) {
                tileentity.u();
            }
        }
        if (!that.world.isStatic && (!that.world.captureBlockStates || block instanceof BlockContainer)) {
            block.onPlace(that.world, l2, j, i3);
        }
        if (block instanceof IContainer) {
            if (that.getType(i, j, k) != block) {
                return false;
            }
            TileEntity tileentity = that.e(i, j, k);
            if (tileentity == null) {
                tileentity = ((IContainer)block).a(that.world, l);
                that.world.setTileEntity(l2, j, i3, tileentity);
            }
            if (tileentity != null) {
                tileentity.u();
            }
        }
        return that.n = true;
    }

}