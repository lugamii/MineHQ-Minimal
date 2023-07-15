package net.lugami.basic.commands.parameter;

import net.lugami.qlib.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoundParameterType implements ParameterType<Sound> {

    public Sound transform(CommandSender sender, String source) {
        for (Sound sound : Sound.values()) {
            if (!sound.name().equalsIgnoreCase(source)) continue;
            return sound;
        }
        sender.sendMessage(ChatColor.RED + "Sound with the name \"" + source + "\" not found.");
        return null;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions = new ArrayList<>();
        for (Sound sound : Sound.values()) {
            completions.add(sound.name());
        }
        return completions;
    }
}

