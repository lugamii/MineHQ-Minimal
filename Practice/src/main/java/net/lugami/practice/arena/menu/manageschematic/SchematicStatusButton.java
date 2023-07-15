package net.lugami.practice.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import net.lugami.qlib.menu.Button;
import net.lugami.practice.Practice;
import net.lugami.practice.arena.Arena;
import net.lugami.practice.arena.ArenaHandler;
import net.lugami.practice.arena.ArenaSchematic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

final class SchematicStatusButton extends Button {

    private final ArenaSchematic schematic;

    SchematicStatusButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + schematic.getName() + " Status";
    }

    @Override
    public List<String> getDescription(Player player) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        int totalCopies = 0;
        int inUseCopies = 0;

        for (Arena arena : arenaHandler.getArenas(schematic)) {
            totalCopies++;

            if (arena.isInUse()) {
                inUseCopies++;
            }
        }

        List<String> description = new ArrayList<>();

        description.add("");
        description.add(ChatColor.GREEN + "Copies: " + ChatColor.WHITE + totalCopies);
        description.add(ChatColor.GREEN + "Copies in use: " + ChatColor.WHITE + inUseCopies);

        return description;
    }

    @Override
    public int getAmount(Player player) {
        ArenaHandler arenaHandler = Practice.getInstance().getArenaHandler();
        return arenaHandler.getArenas(schematic).size();
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NAME_TAG;
    }

}