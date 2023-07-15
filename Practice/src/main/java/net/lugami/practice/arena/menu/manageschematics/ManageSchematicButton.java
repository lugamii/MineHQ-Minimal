package net.lugami.practice.arena.menu.manageschematics;

import com.google.common.base.Preconditions;
import net.lugami.practice.arena.menu.manageschematic.ManageSchematicMenu;
import net.lugami.qlib.menu.Button;
import net.lugami.practice.Practice;
import net.lugami.practice.arena.Arena;
import net.lugami.practice.arena.ArenaHandler;
import net.lugami.practice.arena.ArenaSchematic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

final class ManageSchematicButton extends Button {

    private final ArenaSchematic schematic;

    ManageSchematicButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + "Manage " + schematic.getName();
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
        description.add(ChatColor.GREEN + "Enabled: " + ChatColor.WHITE + (schematic.isEnabled() ? "Yes" : "No"));
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
        return schematic.isEnabled() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        new ManageSchematicMenu(schematic).openMenu(player);
    }

}