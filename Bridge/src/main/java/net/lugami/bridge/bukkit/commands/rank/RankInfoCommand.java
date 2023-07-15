package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.ranks.Rank;

public class RankInfoCommand {

    @Command(names = {"rank info"}, permission = "bridge.rank", description = "Get information about a rank", hidden = true, async = true)
    public static void RankInfoCmd(CommandSender s, @Param(name = "rank") Rank r) {
        s.sendMessage(BukkitAPI.LINE);
        s.sendMessage(r.getColor() + r.getDisplayName() + " Rank §7❘ §fInformation");
        s.sendMessage(BukkitAPI.LINE);
        s.sendMessage("§6Prefix: §f" + r.getPrefix() + " §7(" + ChatColor.stripColor(r.getPrefix().replaceAll("§", "&")) + ")");
        s.sendMessage("§6Suffix: §f" + r.getSuffix() + " §7(" + ChatColor.stripColor(r.getSuffix().replaceAll("§", "&")) + ")");
        s.sendMessage("§6Priority: §f" + r.getPriority());
        s.sendMessage("§6Staff: §f" + r.isStaff());
        s.sendMessage("§6Media: §f" + r.isMedia());
        s.sendMessage("§6Builder: §f" + r.isBuilder());
        s.sendMessage("§6Default: §f" + r.isDefaultRank());
        s.sendMessage(ChatColor.GOLD + "Permissions (" + r.getPermissions().size() + ChatColor.GOLD + "): ");
        if (r.getPermissions().isEmpty()) s.sendMessage(ChatColor.RED + "None...");
        else r.getPermissions().keySet().forEach(str -> {
            s.sendMessage(ChatColor.GRAY + " * " + ChatColor.WHITE + str + ChatColor.GRAY + " [" + r.getPermissions().get(str) + "]");
        });
        s.sendMessage("");
        s.sendMessage("§6Inherits (" + r.getInherits().size() + ChatColor.GOLD + "): " + (r.getInherits().isEmpty() ? "§cNone..." : ""));
        if (!r.getInherits().isEmpty())
            r.getInherits().forEach(rank -> s.sendMessage(" §7* §f" + rank.getColor() + rank.getDisplayName()));

        s.sendMessage(BukkitAPI.LINE);
    }
}
