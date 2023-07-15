package net.lugami.bridge.bukkit.commands.punishment.menu.button;

import com.google.common.collect.Lists;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.util.TimeUtils;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.punishment.Punishment;
import net.lugami.bridge.global.punishment.PunishmentType;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PunishmentButton extends Button
{
    private Punishment punishment;
    private String targetUUID;
    private String targetName;
    private PunishmentType type;
    private Map<Punishment, String> punishments;

    public PunishmentButton(Punishment punishment, PunishmentType type, String targetUUID, String targetName, Map<Punishment, String> punishments){
        this.punishment = punishment;
        this.targetUUID = targetUUID;
        this.targetName = targetName;
        this.type = type;
        this.punishments = punishments;
    }

    public String getName(Player player) {
        return ChatColor.YELLOW + TimeUtils.formatIntoCalendarString(new Date(this.punishment.getTime()));
    }

    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
        String by = punishment.getExecutor().getUsername();
        String actor = "Server" + ChatColor.YELLOW + " : " + ChatColor.RED + (this.punishment.getPunishedServer() != null ? this.punishment.getPunishedServer() : "Website");

        final String randomID = ChatColor.MAGIC + UUID.randomUUID().toString().substring(0, 8);
        description.add(ChatColor.YELLOW + "By: " + ChatColor.RED + (BukkitAPI.getProfile(player).hasPermission("bridge.punishments.view.punisher") ? by : randomID));
        description.add(ChatColor.YELLOW + "Added on: " + ChatColor.RED + actor);
        description.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + this.punishment.getReason());


        if (this.punishment.isActive()) {
            if (!this.punishment.isPermanent()) {
                description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
                description.add(ChatColor.YELLOW + "Time remaining: " + ChatColor.RED + DurationFormatUtils.formatDurationWords(Math.abs(punishment.getRemainingTime()), true, true));
            }
            else {
                description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
                description.add(ChatColor.YELLOW + "This is a permanent punishment.");
            }
        }
        else if (this.punishment.isPardoned()) {
            String removedBy = this.punishment.getPardonedBy().getUsername();
            description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
            description.add(ChatColor.RED + "Removed:");
            description.add(ChatColor.YELLOW + (BukkitAPI.getProfile(player).hasPermission("bridge.punishments.view.punisher") ? removedBy : randomID) + ": " + ChatColor.RED + this.punishment.getPardonedReason());
            description.add(ChatColor.RED + "at " + ChatColor.YELLOW + TimeUtils.formatIntoCalendarString(new Date(this.punishment.getPardonedAt())));
            if (!this.punishment.isPermanent()) {
                description.add("");
                description.add(ChatColor.YELLOW + "Duration: " + TimeUtils.formatIntoDetailedString((int)((this.punishment.getRemainingTime()) / 1000L) + 1));
            }
        }
        else if (!this.punishment.isPermanent() && this.punishment.getRemainingTime() <= 0) {
            description.add(ChatColor.YELLOW + "Duration: " + TimeUtils.formatIntoDetailedString((int)((this.punishment.getRemainingTime()) / 1000L) + 1));
            description.add(ChatColor.GREEN + "Expired");
        }
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @Override
    public byte getDamageValue(Player player) {
        return !this.punishment.isActive() ? DyeColor.RED.getWoolData() : DyeColor.LIME.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
//        new ProofMenu(punishment, PunishmentButton.this.targetUUID,type, PunishmentButton.this.targetName, punishments).openMenu(player);
    }
}
