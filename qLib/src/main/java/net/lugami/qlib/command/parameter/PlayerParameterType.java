package net.lugami.qlib.command.parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import net.lugami.qlib.visibility.FrozenVisibilityHandler;
import net.lugami.qlib.command.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerParameterType implements ParameterType<Player> {

    @Override
    public Player transform(CommandSender sender, String value) {
        if (sender instanceof Player && (value.equalsIgnoreCase("self") || value.equals(""))) {
            return (Player)sender;
        }
        Player player = playerCheck(sender, value);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "No player with the name \"" + value + "\" found.");
            return null;
        }

        if(player.isDisguised() && sender.hasPermission("basic.staff")) sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "DEBUG! " + ChatColor.YELLOW + "The player you are checking is disguised, their real name: " + ((CraftPlayer)player).getProfile().getName());

        return player;
    }

    public Player playerCheck(CommandSender sender, String value) {
        Player player = Bukkit.getServer().getPlayer(value);
        if(player == null) {
            player = Bukkit.getPlayerByDisguise(value);
            if(player == null) {
                return null;
            }
        }

        if(!(sender instanceof Player)) return player;

        if(sender instanceof Player && !FrozenVisibilityHandler.treatAsOnline(player, (Player)sender)) {
            return null;
        }

        if(player.isDisguised()) {
            if(!player.getDisguisedName().equalsIgnoreCase(value) && !sender.hasPermission("basic.staff")) {
                return player;
            }else {
                return null;
            }
        }

        return player;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!FrozenVisibilityHandler.treatAsOnline(player, sender)) continue;
            completions.add(player.getDisguisedName());
        }
        return completions;
    }
}

