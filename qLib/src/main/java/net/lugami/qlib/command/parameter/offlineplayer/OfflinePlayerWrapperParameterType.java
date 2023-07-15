package net.lugami.qlib.command.parameter.offlineplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.lugami.qlib.visibility.FrozenVisibilityHandler;
import net.lugami.qlib.command.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OfflinePlayerWrapperParameterType implements ParameterType<OfflinePlayerWrapper> {

    @Override
    public OfflinePlayerWrapper transform(CommandSender sender, String source) {
        return new OfflinePlayerWrapper(sender, source);
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!FrozenVisibilityHandler.treatAsOnline(player, sender)) continue;
            completions.add(player.getName());
        }
        return completions;
    }
}

