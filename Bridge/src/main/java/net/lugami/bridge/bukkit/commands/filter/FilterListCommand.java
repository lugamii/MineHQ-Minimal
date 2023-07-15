package net.lugami.bridge.bukkit.commands.filter;

import net.lugami.qlib.command.Command;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.filter.FilterAction;
import net.lugami.bridge.global.util.TimeUtil;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class FilterListCommand {

    @Command(names = "filter list", permission = "bridge.filter", description = "List all created filters", hidden = true)
    public static void filter(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 34));
        BridgeGlobal.getFilterHandler().getFilters().forEach(filter -> {
            FancyMessage fancyMessage = new FancyMessage(ChatColor.RED + (isLong(filter.getPattern()) ? filter.getPattern().substring(0, 20) + "..." : filter.getPattern()));
            if (isLong(filter.getPattern())) {
                List<String> lore = new ArrayList<>();
                int index = 0;
                while (index < filter.getPattern().length()) {
                    lore.add(filter.getPattern().substring(index, Math.min(index + 40, filter.getPattern().length())));
                    index += 40;
                }
                fancyMessage.tooltip(lore);
            }
            fancyMessage.then(ChatColor.GRAY + " - " + "[" + filter.getFilterType().name() + "]" + (filter.getFilterAction() == FilterAction.MUTE ? " - Mute: " + TimeUtil.millisToTimer(filter.getMuteTime()) : ""));
            fancyMessage.send(sender);
        });
        sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 34));
    }

    private static boolean isLong(String filter) {
        return filter.length() > 20;
    }
}
