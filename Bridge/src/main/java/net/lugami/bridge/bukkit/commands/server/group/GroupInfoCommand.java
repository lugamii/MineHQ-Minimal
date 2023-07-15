package net.lugami.bridge.bukkit.commands.server.group;

import mkremins.fanciful.FancyMessage;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;

public class GroupInfoCommand {

    @Command(names = {"group info"}, permission = "bridge.group.info", description = "Get information about a server group", hidden = true)
    public static void groupList(CommandSender s, @Param(name = "group", wildcard = true) String g) {
        if (BridgeGlobal.getServerHandler().getServersInGroup(g) == null) {
            s.sendMessage(ChatColor.RED + "There is no such group with the name \"" + g + "\".");
            return;
        }
        s.sendMessage(StringUtils.repeat(ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + "-", 35));
        s.sendMessage(ChatColor.RED + "Servers in the group " + g + ":");
        BridgeGlobal.getServerHandler().getServersInGroup(g).forEach(serv -> new FancyMessage(ChatColor.RED + serv.getName()).tooltip(ChatColor.GRAY + "Click to view information about the server.").command("/serverinfo info " + serv.getName()).send(s));
        s.sendMessage(StringUtils.repeat(ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + "-", 35));
    }
}
