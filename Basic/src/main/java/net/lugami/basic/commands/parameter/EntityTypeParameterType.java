package net.lugami.basic.commands.parameter;

import net.lugami.qlib.command.ParameterType;
import net.lugami.qlib.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EntityTypeParameterType implements ParameterType<EntityType> {

    public EntityType transform(CommandSender sender, String source) {
        EntityType type = EntityUtils.parse(source);
        if (type == null) {
            for (EntityType possibleType : EntityType.values()) {
                if (possibleType.name().equalsIgnoreCase(source)) {
                    return possibleType;
                }
                if (String.valueOf(possibleType.getTypeId()).equalsIgnoreCase(source)) {
                    return possibleType;
                }
                if (!StringUtils.startsWithIgnoreCase(possibleType.name(), source)) continue;
                return possibleType;
            }
        }
        if (type == null) {
            sender.sendMessage(ChatColor.RED + "No entity type with the name " + source + " found.");
            return null;
        }
        return type;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions = new ArrayList<String>();
        for (EntityType mode : EntityType.values()) {
            if (!StringUtils.startsWithIgnoreCase(mode.name(), source)) continue;
            completions.add(mode.name());
        }
        return completions;
    }
}

