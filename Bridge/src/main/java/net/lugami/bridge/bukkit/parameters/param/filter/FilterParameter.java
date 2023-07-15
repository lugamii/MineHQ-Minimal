package net.lugami.bridge.bukkit.parameters.param.filter;

import net.lugami.qlib.command.ParameterType;
import net.lugami.bridge.BridgeGlobal;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.lugami.bridge.global.filter.Filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FilterParameter implements ParameterType<Filter> {

    @Override
    public Filter transform(CommandSender sender, String source) {
        Filter filter = BridgeGlobal.getFilterHandler().getFilter(source);
        if(filter == null) sender.sendMessage(ChatColor.RED + "There is no such filter by the pattern of \"" + source + "\".");
        return filter;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (Filter filter : BridgeGlobal.getFilterHandler().getFilters()) {
            if (StringUtils.startsWithIgnoreCase(filter.getPattern(), source)) {
                completions.add(filter.getPattern());
            }
        }
        return completions;
    }
}
