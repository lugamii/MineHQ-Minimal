package net.lugami.basic.commands.parameter;

import net.lugami.basic.Basic;
import net.lugami.qlib.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageTargetType implements ParameterType<Player> {

    public Player transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return (Player)sender;
        }
        Player player = Bukkit.getServer().getPlayer(source);
        if (player == null || sender instanceof Player && !Basic.getInstance().getMessagingManager().ignoresInvis(player) && !player.canSee(player)) {
            sender.sendMessage(ChatColor.RED + "No player with the name \"" + source + "\" found.");
            return null;
        }
        return player;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Basic.getInstance().getMessagingManager().ignoresInvis(player) && player.canSee(player)) continue;
            completions.add(player.getName());
        }
        return completions;
    }
}

