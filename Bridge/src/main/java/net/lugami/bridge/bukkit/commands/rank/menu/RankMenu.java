package net.lugami.bridge.bukkit.commands.rank.menu;

import lombok.Getter;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import net.lugami.bridge.global.ranks.Rank;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankMenu extends Menu {

    @Getter private Rank rank;

    public RankMenu(Rank rank) {

        this.rank = rank;
        setAutoUpdate(true);

    }

    @Override
    public String getTitle(Player player) {
        return rank.getColor() + rank.getDisplayName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        buttonMap.put(2, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Permissions " + ChatColor.GRAY + "(" + rank.getActivePermissions().size() + ")";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(ChatColor.GRAY + "Click to view the active", ChatColor.GRAY + "permissions on this rank.");
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.PAPER;
            }

            @Override
            public byte getDamageValue(Player var1) {
                return 0;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new PermissionsMenu(rank).openMenu(player);
            }
        });

        buttonMap.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return rank.getColor() + rank.getDisplayName();
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 34),
                        ChatColor.YELLOW + "UUID: " + ChatColor.RED + rank.getUuid().toString(),
                        ChatColor.YELLOW + "Name: " + ChatColor.RED + rank.getName(),
                        "",
                        ChatColor.YELLOW + "Prefix: " + ChatColor.RED + rank.getPrefix().replace("§", "&") + "Color",
                        ChatColor.YELLOW + "Suffix: " + ChatColor.RED + rank.getSuffix().replace("§", "&") + "Color",
                        ChatColor.YELLOW + "Color: " + ChatColor.RED + rank.getColor().replace("§", "&") + "Color",
                        "",
                        ChatColor.YELLOW + "Priority: " + ChatColor.RED + rank.getPriority(),
                        ChatColor.YELLOW + "Staff: " + (rank.isStaff() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"),
                        ChatColor.YELLOW + "Hidden: " + (rank.isHidden() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"),
                        ChatColor.YELLOW + "Grantable: " + (rank.isGrantable() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"),
                        ChatColor.YELLOW + "Default: " + (rank.isDefaultRank() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"),
                        ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 34)
                );
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SIGN;
            }

            @Override
            public byte getDamageValue(Player var1) {
                return 0;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {

            }
        });

        buttonMap.put(6, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.YELLOW + "Players " + ChatColor.GRAY + "(" + rank.getProfilesInRank().size() + ")";
            }

            @Override
            public List<String> getDescription(Player player) {
                return Arrays.asList(
                        ChatColor.GRAY + "List of players that",
                        ChatColor.GRAY + "currently has this rank."
                );
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 3;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                new PlayerMenu(rank).openMenu(player);
            }
        });


        return buttonMap;
    }
}