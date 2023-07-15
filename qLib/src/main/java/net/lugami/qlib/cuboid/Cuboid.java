package net.lugami.qlib.cuboid;

import net.lugami.qlib.qLib;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

import java.util.*;

public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable {

    protected final String worldName;
    protected final int x1;
    protected final int y1;
    protected final int z1;
    protected final int x2;
    protected final int y2;
    protected final int z2;

    public Cuboid(Location l1, Location l2) {
        if (!l1.getWorld().equals((Object)l2.getWorld())) {
            throw new IllegalArgumentException("Locations must be on the same world");
        }
        this.worldName = l1.getWorld().getName();
        this.x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        this.y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        this.z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        this.x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        this.y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        this.z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
    }

    public Cuboid(Location l1) {
        this(l1, l1);
    }

    public Cuboid(Cuboid other) {
        this(other.getWorld().getName(), other.x1, other.y1, other.z1, other.x2, other.y2, other.z2);
    }

    public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = world.getName();
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    private Cuboid(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = worldName;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public Cuboid(Map<String, Object> map) {
        this.worldName = (String)map.get("worldName");
        this.x1 = (Integer)map.get("x1");
        this.x2 = (Integer)map.get("x2");
        this.y1 = (Integer)map.get("y1");
        this.y2 = (Integer)map.get("y2");
        this.z1 = (Integer)map.get("z1");
        this.z2 = (Integer)map.get("z2");
    }

    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("worldName", this.worldName);
        map.put("x1", this.x1);
        map.put("y1", this.y1);
        map.put("z1", this.z1);
        map.put("x2", this.x2);
        map.put("y2", this.y2);
        map.put("z2", this.z2);
        return map;
    }

    public Location getLowerNE() {
        return new Location(this.getWorld(), (double)this.x1, (double)this.y1, (double)this.z1);
    }

    public Location getUpperSW() {
        return new Location(this.getWorld(), (double)this.x2, (double)this.y2, (double)this.z2);
    }

    public List<Block> getBlocks() {
        Iterator<Block> blockI = this.iterator();
        ArrayList<Block> copy = new ArrayList<Block>();
        while (blockI.hasNext()) {
            copy.add(blockI.next());
        }
        return copy;
    }

    public Location getCenter() {
        int x1 = this.getUpperX() + 1;
        int y1 = this.getUpperY() + 1;
        int z1 = this.getUpperZ() + 1;
        return new Location(this.getWorld(), (double)this.getLowerX() + (double)(x1 - this.getLowerX()) / 2.0, (double)this.getLowerY() + (double)(y1 - this.getLowerY()) / 2.0, (double)this.getLowerZ() + (double)(z1 - this.getLowerZ()) / 2.0);
    }

    public World getWorld() {
        World world = qLib.getInstance().getServer().getWorld(this.worldName);
        if (world == null) {
            throw new IllegalStateException("World '" + this.worldName + "' is not loaded");
        }
        return world;
    }

    public int getSizeX() {
        return this.x2 - this.x1 + 1;
    }

    public int getSizeY() {
        return this.y2 - this.y1 + 1;
    }

    public int getSizeZ() {
        return this.z2 - this.z1 + 1;
    }

    public int getLowerX() {
        return this.x1;
    }

    public int getLowerY() {
        return this.y1;
    }

    public int getLowerZ() {
        return this.z1;
    }

    public int getUpperX() {
        return this.x2;
    }

    public int getUpperY() {
        return this.y2;
    }

    public int getUpperZ() {
        return this.z2;
    }

    public Block[] corners() {
        Block[] res = new Block[8];
        World w = this.getWorld();
        res[0] = w.getBlockAt(this.x1, this.y1, this.z1);
        res[1] = w.getBlockAt(this.x1, this.y1, this.z2);
        res[2] = w.getBlockAt(this.x1, this.y2, this.z1);
        res[3] = w.getBlockAt(this.x1, this.y2, this.z2);
        res[4] = w.getBlockAt(this.x2, this.y1, this.z1);
        res[5] = w.getBlockAt(this.x2, this.y1, this.z2);
        res[6] = w.getBlockAt(this.x2, this.y2, this.z1);
        res[7] = w.getBlockAt(this.x2, this.y2, this.z2);
        return res;
    }

    public Block[] minCorners() {
        Block[] res = new Block[4];
        World w = this.getWorld();
        res[0] = w.getBlockAt(this.x1, this.y1, this.z1);
        return res;
    }

    public Cuboid expand(CuboidDirection dir, int amount) {
        switch (dir) {
            case NORTH: {
                return new Cuboid(this.worldName, this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case SOUTH: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2);
            }
            case EAST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2);
            }
            case WEST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount);
            }
            case DOWN: {
                return new Cuboid(this.worldName, this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2);
            }
            case UP: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2);
            }
        }
        throw new IllegalArgumentException("Invalid direction " + (Object)((Object)dir));
    }

    public Cuboid shift(CuboidDirection dir, int amount) {
        return this.expand(dir, amount).expand(dir.opposite(), -amount);
    }

    public Cuboid outset(CuboidDirection dir, int amount) {
        Cuboid c;
        switch (dir) {
            case HORIZONTAL: {
                c = this.expand(CuboidDirection.NORTH, amount).expand(CuboidDirection.SOUTH, amount).expand(CuboidDirection.EAST, amount).expand(CuboidDirection.WEST, amount);
                break;
            }
            case VERTICAL: {
                c = this.expand(CuboidDirection.DOWN, amount).expand(CuboidDirection.UP, amount);
                break;
            }
            case BOTH: {
                c = this.outset(CuboidDirection.HORIZONTAL, amount).outset(CuboidDirection.VERTICAL, amount);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + (Object)((Object)dir));
            }
        }
        return c;
    }

    public Cuboid inset(CuboidDirection dir, int amount) {
        return this.outset(dir, -amount);
    }

    public boolean contains(int x, int y, int z) {
        return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
    }

    public boolean contains(Block b) {
        return this.contains(b.getLocation());
    }

    public boolean contains(Location l) {
        if (!this.worldName.equals(l.getWorld().getName())) {
            return false;
        }
        return this.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    public boolean contains(Entity e) {
        return this.contains(e.getLocation());
    }

    public Cuboid grow(int i) {
        return this.expand(CuboidDirection.NORTH, i).expand(CuboidDirection.SOUTH, i).expand(CuboidDirection.EAST, i).expand(CuboidDirection.WEST, i);
    }

    public int getVolume() {
        return this.getSizeX() * this.getSizeY() * this.getSizeZ();
    }

    public byte getAverageLightLevel() {
        long total = 0L;
        int n = 0;
        for (Block b : this) {
            if (!b.isEmpty()) continue;
            total += (long)b.getLightLevel();
            ++n;
        }
        return n > 0 ? (byte)(total / (long)n) : (byte)0;
    }

    public Cuboid contract() {
        return this.contract(CuboidDirection.DOWN).contract(CuboidDirection.SOUTH).contract(CuboidDirection.EAST).contract(CuboidDirection.UP).contract(CuboidDirection.NORTH).contract(CuboidDirection.WEST);
    }

    public Cuboid contract(CuboidDirection dir) {
        Cuboid face = this.getFace(dir.opposite());
        switch (dir) {
            case DOWN: {
                while (face.containsOnly(0) && face.getLowerY() > this.getLowerY()) {
                    face = face.shift(CuboidDirection.DOWN, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, face.getUpperY(), this.z2);
            }
            case UP: {
                while (face.containsOnly(0) && face.getUpperY() < this.getUpperY()) {
                    face = face.shift(CuboidDirection.UP, 1);
                }
                return new Cuboid(this.worldName, this.x1, face.getLowerY(), this.z1, this.x2, this.y2, this.z2);
            }
            case NORTH: {
                while (face.containsOnly(0) && face.getLowerX() > this.getLowerX()) {
                    face = face.shift(CuboidDirection.NORTH, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, face.getUpperX(), this.y2, this.z2);
            }
            case SOUTH: {
                while (face.containsOnly(0) && face.getUpperX() < this.getUpperX()) {
                    face = face.shift(CuboidDirection.SOUTH, 1);
                }
                return new Cuboid(this.worldName, face.getLowerX(), this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case EAST: {
                while (face.containsOnly(0) && face.getLowerZ() > this.getLowerZ()) {
                    face = face.shift(CuboidDirection.EAST, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, face.getUpperZ());
            }
            case WEST: {
                while (face.containsOnly(0) && face.getUpperZ() < this.getUpperZ()) {
                    face = face.shift(CuboidDirection.WEST, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, face.getLowerZ(), this.x2, this.y2, this.z2);
            }
        }
        throw new IllegalArgumentException("Invalid direction " + (Object)((Object)dir));
    }

    public Cuboid getFace(CuboidDirection dir) {
        switch (dir) {
            case DOWN: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);
            }
            case UP: {
                return new Cuboid(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
            }
            case NORTH: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);
            }
            case SOUTH: {
                return new Cuboid(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case EAST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);
            }
            case WEST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);
            }
        }
        throw new IllegalArgumentException("Invalid direction " + (Object)((Object)dir));
    }

    public boolean containsOnly(int blockId) {
        for (Block b : this) {
            if (b.getTypeId() == blockId) continue;
            return false;
        }
        return true;
    }

    public Cuboid getBoundingCuboid(Cuboid other) {
        if (other == null) {
            return this;
        }
        int xMin = Math.min(this.getLowerX(), other.getLowerX());
        int yMin = Math.min(this.getLowerY(), other.getLowerY());
        int zMin = Math.min(this.getLowerZ(), other.getLowerZ());
        int xMax = Math.max(this.getUpperX(), other.getUpperX());
        int yMax = Math.max(this.getUpperY(), other.getUpperY());
        int zMax = Math.max(this.getUpperZ(), other.getUpperZ());
        return new Cuboid(this.worldName, xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public Block getRelativeBlock(int x, int y, int z) {
        return this.getWorld().getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public Block getRelativeBlock(World w, int x, int y, int z) {
        return w.getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public List<Chunk> getChunks() {
        ArrayList<Chunk> res = new ArrayList<Chunk>();
        World w = this.getWorld();
        int x1 = this.getLowerX() & -16;
        int x2 = this.getUpperX() & -16;
        int z1 = this.getLowerZ() & -16;
        int z2 = this.getUpperZ() & -16;
        for (int x = x1; x <= x2; x += 16) {
            for (int z = z1; z <= z2; z += 16) {
                res.add(w.getChunkAt(x >> 4, z >> 4));
            }
        }
        return res;
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public Cuboid clone() {
        return new Cuboid(this);
    }

    public String toString() {
        return new String("Cuboid: " + this.worldName + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2);
    }

    public List<Block> getWalls() {
        Location minLoc;
        Location maxLoc;
        ArrayList<Block> blocks = new ArrayList<Block>();
        Location min = new Location(this.getWorld(), (double)this.x1, (double)this.y1, (double)this.z1);
        Location max = new Location(this.getWorld(), (double)this.x2, (double)this.y2, (double)this.z2);
        int minX = min.getBlockX();
        int minY = min.getBlockY();
        int minZ = min.getBlockZ();
        int maxX = max.getBlockX();
        int maxY = max.getBlockY();
        int maxZ = max.getBlockZ();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                minLoc = new Location(this.getWorld(), (double)x, (double)y, (double)minZ);
                maxLoc = new Location(this.getWorld(), (double)x, (double)y, (double)maxZ);
                blocks.add(minLoc.getBlock());
                blocks.add(maxLoc.getBlock());
            }
        }
        for (int y = minY; y <= maxY; ++y) {
            for (int z = minZ; z <= maxZ; ++z) {
                minLoc = new Location(this.getWorld(), (double)minX, (double)y, (double)z);
                maxLoc = new Location(this.getWorld(), (double)maxX, (double)y, (double)z);
                blocks.add(minLoc.getBlock());
                blocks.add(maxLoc.getBlock());
            }
        }
        return blocks;
    }

    public List<Block> getFaces() {
        ArrayList<Block> blocks = new ArrayList<Block>();
        Location min = new Location(this.getWorld(), (double)this.x1, (double)this.y1, (double)this.z1);
        Location max = new Location(this.getWorld(), (double)this.x2, (double)this.y2, (double)this.z2);
        int minX = min.getBlockX();
        int minY = min.getBlockY();
        int minZ = min.getBlockZ();
        int maxX = max.getBlockX();
        int maxY = max.getBlockY();
        int maxZ = max.getBlockZ();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                blocks.add(new Location(this.getWorld(), (double)x, (double)y, (double)minZ).getBlock());
                blocks.add(new Location(this.getWorld(), (double)x, (double)y, (double)maxZ).getBlock());
            }
        }
        for (int y = minY; y <= maxY; ++y) {
            for (int z = minZ; z <= maxZ; ++z) {
                blocks.add(new Location(this.getWorld(), (double)minX, (double)y, (double)z).getBlock());
                blocks.add(new Location(this.getWorld(), (double)maxX, (double)y, (double)z).getBlock());
            }
        }
        for (int z = minZ; z <= maxZ; ++z) {
            for (int x = minX; x <= maxX; ++x) {
                blocks.add(new Location(this.getWorld(), (double)x, (double)minY, (double)z).getBlock());
                blocks.add(new Location(this.getWorld(), (double)x, (double)maxY, (double)z).getBlock());
            }
        }
        return blocks;
    }

    public class CuboidIterator
    implements Iterator<Block> {
        private World w;
        private int baseX;
        private int baseY;
        private int baseZ;
        private int x;
        private int y;
        private int z;
        private int sizeX;
        private int sizeY;
        private int sizeZ;

        public CuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.w = w;
            this.baseX = Math.min(x1, x2);
            this.baseY = Math.min(y1, y2);
            this.baseZ = Math.min(z1, z2);
            this.sizeX = Math.abs(x2 - x1) + 1;
            this.sizeY = Math.abs(y2 - y1) + 1;
            this.sizeZ = Math.abs(z2 - z1) + 1;
            this.z = 0;
            this.y = 0;
            this.x = 0;
        }

        @Override
        public boolean hasNext() {
            return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
        }

        @Override
        public Block next() {
            Block b = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
            if (++this.x >= this.sizeX) {
                this.x = 0;
                if (++this.y >= this.sizeY) {
                    this.y = 0;
                    ++this.z;
                }
            }
            return b;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static enum CuboidDirection {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        UP,
        DOWN,
        HORIZONTAL,
        VERTICAL,
        BOTH,
        UNKNOWN;
        

        public CuboidDirection opposite() {
            switch (this) {
                case NORTH: {
                    return SOUTH;
                }
                case EAST: {
                    return WEST;
                }
                case SOUTH: {
                    return NORTH;
                }
                case WEST: {
                    return EAST;
                }
                case HORIZONTAL: {
                    return VERTICAL;
                }
                case VERTICAL: {
                    return HORIZONTAL;
                }
                case UP: {
                    return DOWN;
                }
                case DOWN: {
                    return UP;
                }
                case BOTH: {
                    return BOTH;
                }
            }
            return UNKNOWN;
        }
    }

}

