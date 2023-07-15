package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.qlib.command.Command;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.ranks.Rank;

import java.util.ArrayList;

public class RankListCommand {

    @Command(names = {"rank list"}, permission = "bridge.rank", description = "List all ranks", hidden = true, async = true)
    public static void RankListCmd(CommandSender s) {
        s.sendMessage(BukkitAPI.LINE);
        s.sendMessage("§6§lRanks §f(" + BridgeGlobal.getRankHandler().getRanks().size() + ")");
        s.sendMessage(BukkitAPI.LINE);
        ArrayList<Rank> rankList = new ArrayList<>(BridgeGlobal.getRankHandler().getRanks());
        rankList.sort((o1, o2) -> o2.getPriority() - o1.getPriority());

        rankList.forEach(rank -> {
            if (s instanceof Player) {

                ComponentBuilder cp = new ComponentBuilder(rank.getColor() + rank.getDisplayName() + " §7❘ §f" + rank.getName() + (rank.isHidden() ? " §7[Hidden]" : "")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                        "§6Prefix: §f" + rank.getPrefix() + " §7(" + ChatColor.stripColor(rank.getPrefix().replaceAll("§", "&")) + ")" + "\n" +
                                "§6Suffix: §f" + rank.getSuffix() + " §7(" + ChatColor.stripColor(rank.getSuffix().replaceAll("§", "&")) + ")" + "\n" +
                                "§6Priority: §f" + rank.getPriority() + "\n" +
                                "§6Staff: §f" + rank.isStaff() + "\n" +
                                "§6Media: §f" + rank.isMedia() + "\n" +
                                "§6Builder: §f" + rank.isBuilder() + "\n" +
                                "§6Default: §f" + rank.isDefaultRank() + "\n\n" +
                                "§7§oClick for more information"
                ))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rank info " + rank.getName()));
                ((Player) s).spigot().sendMessage(cp.create());
            } else {
                s.sendMessage(rank.getColor() + rank.getDisplayName() + " §7❘ §f" + rank.getName() + (rank.isHidden() ? " §7[Hidden]" : ""));
                s.sendMessage("§6Prefix: §f" + rank.getPrefix() + " §7(" + ChatColor.stripColor(rank.getPrefix().replaceAll("§", "&")) + ")");
                s.sendMessage("§6Suffix: §f" + rank.getSuffix() + " §7(" + ChatColor.stripColor(rank.getSuffix().replaceAll("§", "&")) + ")");
                s.sendMessage("§6Priority: §f" + rank.getPriority());
                s.sendMessage("§6Staff: §f" + rank.isStaff());
                s.sendMessage("§6Media: §f" + rank.isMedia());
                s.sendMessage("§6Builder: §f" + rank.isBuilder());
                s.sendMessage("§6Default: §f" + rank.isDefaultRank());
                s.sendMessage("");
            }
        });
        s.sendMessage("");
        s.sendMessage(s instanceof Player ? "§7§oHover over the ranks for more information." : "§7§oType /rank info <rank> for more information.");
        s.sendMessage(BukkitAPI.LINE);
    }
}
