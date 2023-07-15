package net.lugami.basic.commands;

import net.lugami.basic.listener.TeleportationListener;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Flag;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.command.parameter.offlineplayer.OfflinePlayerWrapper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportationCommands {

    @Command(names={"teleport", "tp", "tpto", "goto"}, permission="basic.staff", description = "Teleport yourself to a player")
    public static void teleport(Player sender, @Param(name="player") OfflinePlayerWrapper wrapper) {
        wrapper.loadAsync(player -> {
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "No online or offline player with the name " + wrapper.getName() + " found.");
                return;
            }
            sender.teleport(player);
            sender.sendMessage(ChatColor.GOLD + "Teleporting you to " + (player.isOnline() ? "" : "offline player ") + ChatColor.WHITE + player.getDisplayName() + ChatColor.GOLD + ".");
        });
    }

    @Command(names={"tphere", "bring", "s"}, permission="basic.teleport.other", description = "Teleport a player to you")
    public static void tphere(Player sender, @Flag(value={"s", "silentMode"}, description = "Silently teleport the player (staff members always get messaged)") boolean silent, @Param(name="player") Player target) {
        target.teleport(sender);
        sender.sendMessage(ChatColor.GOLD + "Teleporting " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GOLD + " to you.");
        if (!silent || target.hasPermission("basic.staff")) {
            target.sendMessage(ChatColor.GOLD + "Teleporting you to " + ChatColor.WHITE + sender.getDisplayName() + ChatColor.GOLD + ".");
        }
    }

    @Command(names={"back"}, permission="basic.staff", description = "Teleport to your last location")
    public static void back(Player sender) {
        if (!TeleportationListener.getLastLocation().containsKey(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "No previous location recorded.");
            return;
        }
        sender.teleport(TeleportationListener.getLastLocation().get(sender.getUniqueId()));
        sender.sendMessage(ChatColor.GOLD + "Teleporting you to your last recorded location.");
    }

    @Command(names={"tppos"}, permission="basic.teleport", description = "Teleport to coordinates")
    public static void teleport(Player sender, @Param(name="x") double x, @Param(name="y") double y, @Param(name="z") double z, @Param(name="player", defaultValue="self") Player target) {
        if (!sender.equals(target) && !sender.hasPermission("basic.teleport.other")) {
            sender.sendMessage(ChatColor.RED + "No permission to teleport other players.");
            return;
        }
        if (TeleportationCommands.isBlock(x)) {
            x += z >= 0.0 ? 0.5 : -0.5;
        }
        if (TeleportationCommands.isBlock(z)) {
            z += x >= 0.0 ? 0.5 : -0.5;
        }
        target.teleport(new Location(target.getWorld(), x, y, z));
        String location = ChatColor.translateAlternateColorCodes('&', String.format("&e[&f%s&e, &f%s&e, &f%s&e]&6", x, y, z));
        if (!sender.equals(target)) {
            sender.sendMessage(ChatColor.GOLD + "Teleporting " + ChatColor.WHITE + target.getDisplayName() + ChatColor.GOLD + " to " + location + ".");
        }
        target.sendMessage(ChatColor.GOLD + "Teleporting you to " + location + ".");
    }

    private static boolean isBlock(double value) {
        return value % 1.0 == 0.0;
    }
}

