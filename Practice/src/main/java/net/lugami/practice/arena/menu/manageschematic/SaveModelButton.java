package net.lugami.practice.arena.menu.manageschematic;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.lugami.qlib.menu.Button;
import net.lugami.practice.arena.ArenaSchematic;
import net.lugami.practice.arena.WorldEditUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

final class SaveModelButton extends Button {

    private final ArenaSchematic schematic;

    SaveModelButton(ArenaSchematic schematic) {
        this.schematic = Preconditions.checkNotNull(schematic, "schematic");
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Save model";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.YELLOW + "Click to save the model arena"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PISTON_BASE;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();

        try {
            WorldEditUtils.save(schematic, schematic.getModelArenaLocation());
        } catch (Exception ex) {
            player.sendMessage(ChatColor.RED + "Failed to save " + schematic.getName() + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
