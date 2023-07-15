package net.lugami.bridge.bukkit.commands.punishment.menu.staffhistory;

import com.google.common.collect.Maps;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.punishment.Punishment;
import net.lugami.bridge.global.punishment.PunishmentType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class MainStaffPunishmentListMenu extends Menu {

    private String targetUUID;
    private String targetName;


    public String getTitle(Player player) {
        return ChatColor.BLUE + "Staff History - " + this.targetName;
    }

        public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
            if (player.hasPermission("bridge.punishments.view.blacklist")) {
                buttons.put(0, this.button(PunishmentType.WARN));
                buttons.put(2, this.button(PunishmentType.KICK));
                buttons.put(4, this.button(PunishmentType.MUTE));
                buttons.put(6, this.button(PunishmentType.BAN));
                buttons.put(8, this.button(PunishmentType.BLACKLIST));
            }
            else {
                buttons.put(1, this.button(PunishmentType.WARN));
                buttons.put(3, this.button(PunishmentType.KICK));
                buttons.put(5, this.button(PunishmentType.MUTE));
                buttons.put(7, this.button(PunishmentType.BAN));
            }
            return buttons;
        }

    private Button button(PunishmentType type) {
        return new Button() {
            public String getName(Player player) {
                return ChatColor.RED + type.getDisplayName() + "s";
            }

            public List<String> getDescription(Player player) {
                return null;
            }

            public Material getMaterial(Player player) {
                return Material.WOOL;
            }

            public byte getDamageValue(Player player) {
                if (type == PunishmentType.WARN) {
                    return DyeColor.YELLOW.getWoolData();
                }
                if (type == PunishmentType.KICK) {
                    return DyeColor.GRAY.getWoolData();
                }
                if (type == PunishmentType.MUTE) {
                    return DyeColor.ORANGE.getWoolData();
                }
                if (type == PunishmentType.BAN) {
                    return DyeColor.RED.getWoolData();
                }
                return DyeColor.BLACK.getWoolData();
            }

            public void clicked(Player player, int i, ClickType clickType) {
                player.closeInventory();
                player.sendMessage(ChatColor.GREEN + "Loading " + MainStaffPunishmentListMenu.this.targetName + "'s " + type.getDisplayName() + " Staff History...");

                List<Punishment> allPunishments = new ArrayList<>(BukkitAPI.getProfile(UUID.fromString(targetUUID)).getStaffPunishments());
                Bukkit.getScheduler().scheduleAsyncDelayedTask(Bridge.getInstance(), () -> {
                    allPunishments.sort((first, second) -> Long.compare(second.getTime(), first.getTime()));
                    LinkedHashMap<Punishment, String> punishments = new LinkedHashMap<>();
                    allPunishments.stream().filter(punishment -> punishment.getPunishmentType().equals(type)).forEach(punishment -> punishments.put(punishment, punishment.getTarget().getUsername()));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Bridge.getInstance(), () -> new StaffPunishmentListMenu(targetUUID, targetName, type, punishments).openMenu(player));
                });
            }
        };
    }

    public MainStaffPunishmentListMenu(String targetUUID, String targetName) {
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        setAutoUpdate(true);
    }
}
