package net.lugami.basic.commands.parameter;

import net.lugami.qlib.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeParameterType implements ParameterType<GameMode> {

    public GameMode transform(CommandSender sender, String source) {
        if (source.equals("-0*toggle*0-") && sender instanceof Player) {
            return ((Player)sender).getGameMode() != GameMode.CREATIVE ? GameMode.CREATIVE : GameMode.SURVIVAL;
        }
        for (GameMode mode : GameMode.values()) {
            if (mode.name().equalsIgnoreCase(source)) {
                return mode;
            }
            if (String.valueOf(mode.getValue()).equalsIgnoreCase(source)) {
                return mode;
            }
            if (!StringUtils.startsWithIgnoreCase(mode.name(), source)) continue;
            return mode;
        }
        sender.sendMessage(ChatColor.RED + "No gamemode with the name " + source + " found.");
        return null;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions = new ArrayList<>();
        for (GameMode mode : GameMode.values()) {
            if (!StringUtils.startsWithIgnoreCase(mode.name(), source)) continue;
            completions.add(mode.name());
        }
        return completions;
    }
}

