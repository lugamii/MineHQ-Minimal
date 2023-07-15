package net.lugami.bridge.bukkit.commands.filter;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.filter.Filter;
import net.lugami.bridge.global.filter.FilterAction;
import net.lugami.bridge.global.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class FilterTestCommand {

    @Command(names = "filter test", permission = "bridge.filter", description = "Test a filter", hidden = true)
    public static void test(CommandSender sender, @Param(name = "message", wildcard = true) String message) {
        Filter filter = BridgeGlobal.getFilterHandler().isViolatingFilter(message);
        if(filter == null) {
            sender.sendMessage(ChatColor.GREEN + "This message is not filtered.");
            return;
        }

        sender.sendMessage(ChatColor.RED + "Your message currently flags for the filter: " + filter.getPattern());
        if(filter.getFilterAction() == FilterAction.MUTE) sender.sendMessage(ChatColor.RED + "This message would of caused you to be muted for " + TimeUtil.millisToTimer(filter.getMuteTime()));
    }
}
