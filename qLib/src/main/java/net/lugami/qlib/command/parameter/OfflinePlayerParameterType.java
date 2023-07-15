package net.lugami.qlib.command.parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.lugami.qlib.qLib;
import net.lugami.qlib.visibility.FrozenVisibilityHandler;
import net.lugami.qlib.command.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OfflinePlayerParameterType implements ParameterType<OfflinePlayer> {

    @Override
    public OfflinePlayer transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return (Player)sender;
        }

        Player player = playerCheck(sender, source);
        if(player != null && player.isOnline()) return player;

        return qLib.getInstance().getServer().getOfflinePlayer(source);
    }

    public Player playerCheck(CommandSender sender, String value) {
        Player player = Bukkit.getServer().getPlayer(value);
        if(player == null) {
            return null;
        }

        if(!(sender instanceof Player)) return player;

        if(sender instanceof Player && !FrozenVisibilityHandler.treatAsOnline(player, (Player)sender)) {
            return null;
        }

        if(player.isDisguised()) {
            if(sender == player || sender.hasPermission("basic.staff")) {
                return player;
            }
            if(!player.getDisguisedName().toLowerCase().startsWith(value.toLowerCase())) return null;
        }

        return player;
    }


    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions = new ArrayList<String>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!FrozenVisibilityHandler.treatAsOnline(player, sender)) continue;
            completions.add(player.getName());
        }
        return completions;
    }
}

