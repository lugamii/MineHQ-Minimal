package net.lugami.qlib.command.bukkit;

import java.util.Set;

import net.lugami.qlib.command.CommandNode;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;

public class FrozenHelpTopic extends HelpTopic {

    private final CommandNode node;

    public FrozenHelpTopic(CommandNode node, Set<String> aliases) {
        this.node = node;
        this.name = "/" + node.getName();
        String description = node.getDescription();
        this.shortText = description.length() < 32 ? description : description.substring(0, 32);
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.GOLD);
        sb.append("Description: ");
        sb.append(ChatColor.WHITE);
        sb.append(node.getDescription());
        sb.append("\n");
        sb.append(ChatColor.GOLD);
        sb.append("Usage: ");
        sb.append(ChatColor.WHITE);
        sb.append(node.getUsageForHelpTopic());
        if (aliases != null && aliases.size() > 0) {
            sb.append("\n");
            sb.append(ChatColor.GOLD);
            sb.append("Aliases: ");
            sb.append(ChatColor.WHITE);
            sb.append(StringUtils.join(aliases, ", "));
        }
        this.fullText = sb.toString();
    }

    public boolean canSee(CommandSender commandSender) {
        return this.node.canUse(commandSender);
    }
}

