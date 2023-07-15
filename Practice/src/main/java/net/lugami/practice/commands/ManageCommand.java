package net.lugami.practice.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.lugami.practice.arena.menu.manageschematics.ManageSchematicsMenu;
import net.lugami.practice.kittype.menu.manage.ManageKitTypeMenu;
import net.lugami.practice.kittype.menu.select.SelectKitTypeMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;

public final class ManageCommand {

    @Command(names = {"manage"}, permission = "practice.admin")
    public static void manage(Player sender) {
        new ManageMenu().openMenu(sender);
    }

    public static class ManageMenu extends Menu {

        public ManageMenu() {
            super("Admin Management Menu");
        }

        @Override
        public Map<Integer, Button> getButtons(Player player) {
            return ImmutableMap.of(
                3, new ManageKitButton(),
                5, new ManageArenaButton()
            );
        }

    }

    private static class ManageKitButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + "Manage kit type definitions";
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.DIAMOND_SWORD;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();

            new SelectKitTypeMenu((kitType) -> {
                player.closeInventory();
                new ManageKitTypeMenu(kitType).openMenu(player);
            }, false, "Manage Kit Type...").openMenu(player);
        }

    }

    private static class ManageArenaButton extends Button {

        @Override
        public String getName(Player player) {
            return ChatColor.YELLOW + "Manage the arena grid";
        }

        @Override
        public List<String> getDescription(Player player) {
            return ImmutableList.of();
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.IRON_PICKAXE;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            new ManageSchematicsMenu().openMenu(player);
        }

    }

}