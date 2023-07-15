package net.lugami.bridge.bukkit.commands.punishment.menu.staffhistory;

import com.google.common.collect.Lists;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.util.TimeUtils;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.punishment.Punishment;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class StaffPunishmentButton extends Button {
    
    private final Punishment punishment;

    public StaffPunishmentButton(Punishment punishment){
        this.punishment = punishment;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + TimeUtils.formatIntoCalendarString(new Date(punishment.getTime()));
    }

    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));
        String by = punishment.getExecutor().getUsername();
        String actor = "Server" + ChatColor.YELLOW + " : " + ChatColor.RED + this.punishment.getPunishedServer();

        final String randomID = ChatColor.MAGIC + UUID.randomUUID().toString().substring(0, 8);
        description.add(ChatColor.YELLOW + "Target: " + ChatColor.RED + punishment.getTarget().getUsername());
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

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        return !this.punishment.isActive() ? DyeColor.RED.getWoolData() : DyeColor.LIME.getWoolData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
//        new ProofMenu(punishment, punishment.getTarget().getUuid().toString(), punishment.getPunishmentType(), punishment.getTarget().getUsername().toString(), null).openMenu(player);
    }
}