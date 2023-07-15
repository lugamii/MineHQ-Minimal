package net.lugami.qlib.command.parameter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Set;

import net.lugami.qlib.command.ParameterType;
import net.lugami.qlib.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LongParameterType implements ParameterType<Long> {

    @Override
    public Long transform(CommandSender sender, String value) {
        long time = TimeUtil.parseTime(value);
        if(time == -1L) {
            sender.sendMessage(ChatColor.RED + "You have provided an invalid timestamp.");
            return null;
        }
        return time;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return ImmutableList.of();
    }
}

