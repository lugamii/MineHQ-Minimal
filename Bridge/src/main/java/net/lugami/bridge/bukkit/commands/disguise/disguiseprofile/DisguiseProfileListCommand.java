package net.lugami.bridge.bukkit.commands.disguise.disguiseprofile;

import net.lugami.qlib.command.Command;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.disguise.DisguiseProfile;

import java.util.ArrayList;

public class DisguiseProfileListCommand {

    @Command(names = {"disguiseprofile list"}, permission = "bridge.disguise.admin", description = "List all created disguise profiles", hidden = true)
    public static void disguiseprofilelist(CommandSender s) {
        s.sendMessage(BukkitAPI.LINE);
        s.sendMessage("§6§lDisguise Profiles §f(" + BridgeGlobal.getDisguiseManager().getDisguiseProfiles().size() + ")");
        s.sendMessage(BukkitAPI.LINE);
        if (BridgeGlobal.getDisguiseManager().getDisguiseProfiles().isEmpty()) {
            s.sendMessage(ChatColor.RESET + "No disguise profiles were created yet");
        } else {
            ArrayList<DisguiseProfile> disguiseProfiles = new ArrayList<>(BridgeGlobal.getDisguiseManager().getDisguiseProfiles().values());

            disguiseProfiles.forEach(disguiseProfile -> {
                if (s instanceof Player) {

                    ComponentBuilder cp = new ComponentBuilder(disguiseProfile.getDisplayName() + " §7❘ §f" + disguiseProfile.getName()).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                            "§6Name: §f" + disguiseProfile.getName() + " §7(" + ChatColor.stripColor(disguiseProfile.getDisplayName().replaceAll("§", "&")) + ")" + "\n" +
                                    "§6Display Name: §f" + disguiseProfile.getDisplayName() + " §7(" + ChatColor.stripColor(disguiseProfile.getDisplayName().replaceAll("§", "&")) + ")" + "\n" +
                                    "§6Skin: §f" + disguiseProfile.getSkinName() + "\n\n" +
                                    "§7§oClick for more information"
                    ))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/disguiseprofile info " + disguiseProfile.getName()));
                    ((Player) s).spigot().sendMessage(cp.create());
                } else {
                    s.sendMessage(disguiseProfile.getDisplayName() + " §7❘ §f" + disguiseProfile.getName());
                    s.sendMessage("§6Name: §f" + disguiseProfile.getName() + " §7(" + ChatColor.stripColor(disguiseProfile.getDisplayName().replaceAll("§", "&")) + ")");
                    s.sendMessage("§6Display Name: §f" + disguiseProfile.getDisplayName() + " §7(" + ChatColor.stripColor(disguiseProfile.getDisplayName().replaceAll("§", "&")) + ")");
                    s.sendMessage("§6Skin: §f" + disguiseProfile.getSkinName());
                    s.sendMessage("");
                }
            });
            s.sendMessage("");
            s.sendMessage(s instanceof Player ? "§7§oHover over the disguise profiles for more information." : "§7§oType /disguise profileinfo <disguiseProfile> for more information.");
            s.sendMessage(BukkitAPI.LINE);
        }
    }
}
