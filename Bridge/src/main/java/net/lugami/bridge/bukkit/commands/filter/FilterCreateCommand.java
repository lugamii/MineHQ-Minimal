package net.lugami.bridge.bukkit.commands.filter;

import net.lugami.bridge.bukkit.parameters.packets.filter.FilterCreatePacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.xpacket.FrozenXPacketHandler;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.filter.Filter;
import net.lugami.bridge.global.filter.FilterType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class FilterCreateCommand {
    @Command(names = "filter create", permission = "bridge.filter", description = "Create a filter", hidden = true)
    public static void create(CommandSender sender, @Param(name = "filterType") FilterType filterType, @Param(name = "pattern", wildcard = true) String pattern) {
        Filter filter = BridgeGlobal.getFilterHandler().getFilter(pattern);
        if (filter != null) {
            sender.sendMessage(ChatColor.RED + "This filter already exists");
            return;
        }
        (filter = new Filter(filterType, pattern)).save();
        BridgeGlobal.getFilterHandler().addFilter(filter);
        FrozenXPacketHandler.sendToAll(new FilterCreatePacket(filter, Bukkit.getServerName()));
        sender.sendMessage(ChatColor.GREEN + "Successfully created the filter.");
    }
}
