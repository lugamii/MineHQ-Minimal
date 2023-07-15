package net.lugami.practice.arena;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import lombok.Getter;
import net.lugami.qlib.cuboid.Cuboid;
import net.lugami.practice.Practice;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public final class ArenaGrid {

    public static final Vector STARTING_POINT = new Vector(1_000, 80, 1_000);

    public static final int GRID_SPACING_X = 300;
    public static final int GRID_SPACING_Z = 300;

    @Getter
    private boolean busy = false;

    public void scaleCopies(ArenaSchematic schematic, int desiredCopies, Runnable callback) {
        if (busy) {
            throw new IllegalStateException("Grid is busy!");
        }

        busy = true;

        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        int currentCopies = arenaHandler.countArenas(schematic);

        Runnable saveWrapper = () -> {
            try {
                arenaHandler.saveArenas();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            busy = false;
            callback.run();
        };

        if (currentCopies > desiredCopies) {
            deleteArenas(schematic, currentCopies, currentCopies - desiredCopies, saveWrapper);
        } else if (currentCopies < desiredCopies) {
            createArenas(schematic, currentCopies, desiredCopies - currentCopies, saveWrapper);
        } else {
            saveWrapper.run();
        }
    }

    private void createArenas(ArenaSchematic schematic, int currentCopies, int toCreate, Runnable callback) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();

        new BukkitRunnable() {

            int created = 0;

            @Override
            public void run() {
                int copy = currentCopies + created + 1; // arenas are 1-indexed, not 0
                int xStart = STARTING_POINT.getBlockX() + (GRID_SPACING_X * schematic.getGridIndex());
                int zStart = STARTING_POINT.getBlockZ() + (GRID_SPACING_Z * copy);

                try {
                    Arena created = createArena(schematic, xStart, zStart, copy);
                    arenaHandler.registerArena(created);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    callback.run();
                    cancel();
                }

                created++;

                if (created == toCreate) {
                    callback.run();
                    cancel();
                }
            }

        }.runTaskTimer(Practice.getInstance(), 8L, 8L);
    }

    private void deleteArenas(ArenaSchematic schematic, int currentCopies, int toDelete, Runnable callback) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();

        new BukkitRunnable() {

            int deleted = 0;

            @Override
            public void run() {
                int copy = currentCopies - deleted;
                Arena existing = arenaHandler.getArena(schematic, copy);

                if (existing != null) {
                    WorldEditUtils.clear(existing.getBounds());
                    arenaHandler.unregisterArena(existing);
                }

                deleted++;

                if (deleted == toDelete) {
                    callback.run();
                    cancel();
                }
            }

        }.runTaskTimer(Practice.getInstance(), 8L, 8L);
    }

    private Arena createArena(ArenaSchematic schematic, int xStart, int zStart, int copy) {
        Vector pasteAt = new Vector(xStart, STARTING_POINT.getY(), zStart);
        CuboidClipboard clipboard;

        try {
            clipboard = WorldEditUtils.paste(schematic, pasteAt);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        Location lowerCorner = WorldEditUtils.vectorToLocation(pasteAt);
        Location upperCorner = WorldEditUtils.vectorToLocation(pasteAt.add(clipboard.getSize()));

        return new Arena(
            schematic.getName(),
            copy,
            new Cuboid(lowerCorner, upperCorner)
        );
    }

    public void free() {
        busy = false;
    }

}