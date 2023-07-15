package net.lugami.basic.commands.parameter;

import net.lugami.qlib.command.ParameterType;
import net.lugami.qlib.visibility.FrozenVisibilityHandler;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MonitorTarget {
    
    private final String name;

    public boolean isStop() {
        return this.name.equals("-");
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(this.name);
    }

    @ConstructorProperties(value={"name"})
    public MonitorTarget(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static class Type
    implements ParameterType<MonitorTarget> {
        public MonitorTarget transform(CommandSender sender, String source) {
            if (source.equals("-")) {
                return new MonitorTarget("-");
            }
            if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
                return new MonitorTarget(sender.getName());
            }
            Player player = Bukkit.getServer().getPlayer(source);
            if (!(player == null || sender instanceof Player && !((Player)sender).canSee(player) && player.hasMetadata("invisible"))) {
                return new MonitorTarget(player.getName());
            }
            sender.sendMessage(ChatColor.RED + "No player with the name \"" + source + "\" found.");
            return null;
        }

        public List<String> tabComplete(Player sender, Set<String> flags, String source) {
            ArrayList<String> completions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!FrozenVisibilityHandler.treatAsOnline(player, sender)) continue;
                completions.add(player.getName());
            }
            return completions;
        }
    }
}

