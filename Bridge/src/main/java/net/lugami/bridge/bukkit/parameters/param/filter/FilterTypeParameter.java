package net.lugami.bridge.bukkit.parameters.param.filter;

import net.lugami.qlib.command.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.lugami.bridge.global.filter.FilterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FilterTypeParameter implements ParameterType<FilterType> {

    @Override
    public FilterType transform(CommandSender sender, String source) {
        FilterType filterType = null;
        try {
            filterType = FilterType.valueOf(source);
        }catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "There is no such filter type by the name of \"" + source + "\".");
        }
        return filterType;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (FilterType filterType : FilterType.values()) {
            if (StringUtils.startsWithIgnoreCase(filterType.name(), source)) {
                completions.add(filterType.name());
            }
        }
        return completions;
    }
}
