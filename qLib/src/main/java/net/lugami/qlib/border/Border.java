package net.lugami.qlib.border;

import net.lugami.qlib.border.event.BorderChangeEvent;
import net.lugami.qlib.cuboid.Cuboid;
import net.lugami.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

public class Border {
    private final Location origin;
    private Material material;
    private int size;
    private int height;
    private boolean wrapTerrain = false;
    private BorderConfiguration borderConfiguration = BorderConfiguration.DEFAULT_CONFIGURATION;
    private Effect particle;
    private Cuboid physicalBounds;
    private final BorderTask borderTask;
    private static final boolean[] airBlocks = new boolean[256];

    public Border(Location origin, Material material, int size, int height) {
        this.origin = origin;
        this.size = size;
        this.height = height;
        this.material = material == null ? Material.BEDROCK : material;
        this.physicalBounds = new Cuboid(origin.clone().add(size + 1, (double)origin.getWorld().getMaxHeight() - origin.getY(), size + 1), origin.clone().subtract(size + 1, origin.getY(), size + 1));
        this.borderTask = new BorderTask(this);
        FrozenBorderHandler.addBorder(this);
    }

    public Cuboid contract(int amount) {
        this.size -= amount;
        Cuboid prev = this.physicalBounds.clone();
        this.physicalBounds = this.physicalBounds.inset(Cuboid.CuboidDirection.HORIZONTAL, amount);
        return prev;
    }

    public Cuboid expand(int amount) {
        this.size += amount;
        Cuboid prev = this.physicalBounds.clone();
        this.physicalBounds = this.physicalBounds.expand(Cuboid.CuboidDirection.NORTH, amount).expand(Cuboid.CuboidDirection.SOUTH, amount).expand(Cuboid.CuboidDirection.EAST, amount).expand(Cuboid.CuboidDirection.WEST, amount);
        return prev;
    }

    public Cuboid setSize(int size) {
        return this.setSize(size, true);
    }

    public Cuboid setSize(int size, boolean callEvent) {
        return this.setSize(size, this.height, callEvent);
    }

    public Cuboid setSize(int size, int height, boolean callEvent) {
        int previousSize = this.size;
        this.size = size;
        this.height = height;
        Cuboid prev = this.physicalBounds.clone();
        this.physicalBounds = new Cuboid(this.origin.clone().add(size + 1, (double)this.origin.getWorld().getMaxHeight() - this.origin.getY(), size + 1), this.origin.clone().subtract(size + 1, this.origin.getY(), size + 1));
        if (callEvent) {
            Bukkit.getPluginManager().callEvent(new BorderChangeEvent(this, previousSize, prev, BorderTask.BorderAction.SET));
        }
        return prev;
    }

    public boolean contains(Block block) {
        return this.contains(block.getX(), block.getZ());
    }

    public boolean contains(Entity entity) {
        return this.contains(entity.getLocation());
    }

    public boolean contains(Location location) {
        return this.contains(location.getBlockX(), location.getBlockZ());
    }

    public boolean contains(int x, int z) {
        return x > this.physicalBounds.getLowerX() && x < this.physicalBounds.getUpperX() && z > this.physicalBounds.getLowerZ() && z < this.physicalBounds.getUpperZ();
    }

    public void fill() {
        World world = this.origin.getWorld();
        int xMin = this.physicalBounds.getLowerX();
        int xMax = this.physicalBounds.getUpperX();
        int zMin = this.physicalBounds.getLowerZ();
        int zMax = this.physicalBounds.getUpperZ();
        int tick = 0;
        int chunksPerTick = 20;
        int chunkZ = zMin >> 4;
        while (chunkZ <= zMax >> 4) {
            int cz = chunkZ++;
            Bukkit.getScheduler().runTaskLater(qLib.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(xMin >> 4, cz);
                for (int z = Math.max(zMin, cz << 4); z < Math.min(zMax, cz + 1 << 4); ++z) {
                    this.fillAtXZ(world, chunk, xMin, z);
                }
            }, tick++ / chunksPerTick);
            Bukkit.getScheduler().runTaskLater(qLib.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(xMax >> 4, cz);
                for (int z = Math.max(zMin + 1, cz << 4); z < Math.min(zMax + 1, cz + 1 << 4); ++z) {
                    this.fillAtXZ(world, chunk, xMax, z);
                }
            }, tick++ / chunksPerTick);
        }
        int chunkX = xMin >> 4;
        while (chunkX <= xMax >> 4) {
            int cx = chunkX++;
            Bukkit.getScheduler().runTaskLater(qLib.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(cx, zMin >> 4);
                for (int x = Math.max(xMin + 1, cx << 4); x < Math.min(xMax + 1, cx + 1 << 4); ++x) {
                    this.fillAtXZ(world, chunk, x, zMin);
                }
            }, tick++ / chunksPerTick);
            Bukkit.getScheduler().runTaskLater(qLib.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(cx, zMax >> 4);
                for (int x = Math.max(xMin, cx << 4); x < Math.min(xMax, cx + 1 << 4); ++x) {
                    this.fillAtXZ(world, chunk, x, zMax);
                }
            }, tick++ / chunksPerTick);
        }
    }

    private void fillAtXZ(World world, Chunk chunk, int x, int z) {
        if (this.wrapTerrain) {
            int y;
            for (y = world.getHighestBlockYAt(x, z); airBlocks[chunk.getBlock(x, y, z).getType().getId()] && y > 0; --y) {
            }
            y += this.height;
            while (y >= 0) {
                chunk.getBlock(x, y, z).setTypeIdAndData(this.material.getId(), (byte)0, false);
                --y;
            }
        } else {
            for (int y = 0; y <= this.origin.getBlockY() + this.height; ++y) {
                chunk.getBlock(x, y, z).setTypeIdAndData(this.material.getId(), (byte)0, false);
            }
        }
    }

    public Location correctLocation(Location location) {
        Cuboid cuboid = this.getPhysicalBounds();
        int validX = location.getBlockX();
        int validZ = location.getBlockZ();
        EnsureAction xAction = null;
        EnsureAction zAction = null;
        if (location.getBlockX() + 2 > cuboid.getUpperX()) {
            xAction = EnsureAction.DECREASE;
            validX = xAction.apply(cuboid.getUpperX(), 4);
        } else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
            xAction = EnsureAction.INCREASE;
            validX = xAction.apply(cuboid.getLowerX(), 4);
        }
        if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
            zAction = EnsureAction.DECREASE;
            validZ = zAction.apply(cuboid.getUpperZ(), 4);
        } else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
            zAction = EnsureAction.INCREASE;
            validZ = zAction.apply(cuboid.getLowerZ(), 4);
        }
        int validY = location.getWorld().getHighestBlockYAt(validX, validZ);
        Location validLoc = new Location(location.getWorld(), (double)validX + 0.5, (double)validY + 0.5, (double)validZ + 0.5);
        int tries = 0;
        while (!Border.isSafe(validLoc) && tries++ < 30) {
            if (xAction != null) {
                validX = xAction.apply(validX, 1);
            }
            if (zAction != null) {
                validZ = zAction.apply(validZ, 1);
            }
            validY = location.getWorld().getHighestBlockYAt(validX, validZ);
            validLoc = new Location(location.getWorld(), (double)validX + 0.5, (double)validY + 0.5, (double)validZ + 0.5);
        }
        validLoc.setPitch(location.getPitch());
        validLoc.setYaw(location.getYaw());
        return validLoc;
    }

    private static boolean isSafe(Location location) {
        return location.getBlock().getRelative(BlockFace.DOWN).getType().isSolid() && location.getBlock().isEmpty() && location.getBlock().getRelative(BlockFace.UP).isEmpty();
    }

    public Cuboid getPhysicalBounds() {
        return this.physicalBounds.clone();
    }

    public BorderTask getBorderTask() {
        return this.borderTask;
    }

    public Location getOrigin() {
        return this.origin;
    }

    public Material getMaterial() {
        return this.material;
    }

    public Border setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public int getSize() {
        return this.size;
    }

    public int getHeight() {
        return this.height;
    }

    public Border setHeight(int height) {
        this.height = height;
        return this;
    }

    public boolean isWrapTerrain() {
        return this.wrapTerrain;
    }

    public Border setWrapTerrain(boolean wrapTerrain) {
        this.wrapTerrain = wrapTerrain;
        return this;
    }

    public BorderConfiguration getBorderConfiguration() {
        return this.borderConfiguration;
    }

    public Border setBorderConfiguration(BorderConfiguration borderConfiguration) {
        this.borderConfiguration = borderConfiguration;
        return this;
    }

    public Effect getParticle() {
        return this.particle;
    }

    public Border setParticle(Effect particle) {
        this.particle = particle;
        return this;
    }

    static {
        Border.airBlocks[Material.LOG.getId()] = true;
        Border.airBlocks[Material.LOG_2.getId()] = true;
        Border.airBlocks[Material.LEAVES.getId()] = true;
        Border.airBlocks[Material.LEAVES_2.getId()] = true;
        Border.airBlocks[Material.HUGE_MUSHROOM_1.getId()] = true;
        Border.airBlocks[Material.HUGE_MUSHROOM_2.getId()] = true;
        Border.airBlocks[Material.SNOW.getId()] = true;
        for (Material material : Material.values()) {
            if (!material.isBlock() || material.isSolid()) continue;
            Border.airBlocks[material.getId()] = true;
        }
        Border.airBlocks[Material.WATER.getId()] = false;
        Border.airBlocks[Material.STATIONARY_WATER.getId()] = false;
        Border.airBlocks[Material.LAVA.getId()] = false;
        Border.airBlocks[Material.STATIONARY_LAVA.getId()] = false;
    }

    enum EnsureAction {
        INCREASE,
        DECREASE;
        

        public int apply(int previous, int amount) {
            if (this == INCREASE) {
                return previous + amount;
            }
            return previous - amount;
        }
    }

}

