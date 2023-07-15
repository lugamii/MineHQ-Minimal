package net.lugami.bridge.bukkit.commands.filter;

import lombok.Getter;
import net.lugami.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class ToggleFilterCommand {

    @Getter
    private final static Map<UUID, String> filter = new HashMap<>();

    @Command(names = "togglefilter", permission = "basic.staff", description = "Toggle filter alerts", hidden = true)
    public static void toggle(Player player) {
        String value = filter.getOrDefault(player.getUniqueId(), "global");
        switch (value) {
            case "global": {
                filter.put(player.getUniqueId(), "server");
                player.sendMessage(ChatColor.YELLOW + "You have now limited your filter messages to " + ChatColor.LIGHT_PURPLE + "local server" + ChatColor.YELLOW + ".");
                return;
            }

            case "server": {
                filter.put(player.getUniqueId(), "off");
                player.sendMessage(ChatColor.RED + "You have disabled your filter messages.");
                return;
            }

            case "off": {
                filter.put(player.getUniqueId(), "global");
                player.sendMessage(ChatColor.YELLOW + "You have now limited your filter messages to " + ChatColor.LIGHT_PURPLE + "global network" + ChatColor.YELLOW + ".");
            }
        }
    }
}