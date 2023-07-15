package net.lugami.qlib.command.parameter;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Set;

import net.lugami.qlib.command.ParameterType;
import net.lugami.qlib.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemStackParameterType implements ParameterType<ItemStack> {

    @Override
    public ItemStack transform(CommandSender sender, String source) {
        ItemStack item = ItemUtils.get(source);
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "No item with the name " + source + " found.");
            return null;
        }
        return item;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String prefix) {
        return ImmutableList.of();
    }
}

