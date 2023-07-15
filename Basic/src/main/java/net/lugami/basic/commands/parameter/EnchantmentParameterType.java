package net.lugami.basic.commands.parameter;

import net.lugami.basic.util.EnchantmentWrapper;
import net.lugami.qlib.command.ParameterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class EnchantmentParameterType implements ParameterType<Enchantment> {

    public Enchantment transform(CommandSender sender, String source) {
        EnchantmentWrapper enchantment = EnchantmentWrapper.parse(source);
        if (enchantment == null) {
            sender.sendMessage(ChatColor.RED + "No enchantment with the name " + source + " found.");
            return null;
        }
        return enchantment.getBukkitEnchantment();
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        ArrayList<String> completions = new ArrayList<>();
        for (EnchantmentWrapper enchantment : EnchantmentWrapper.values()) {
            for (String str : enchantment.getParse()) {
                if (!StringUtils.startsWithIgnoreCase(str, source)) continue;
                completions.add(str);
                return completions;
            }
            if (StringUtils.startsWithIgnoreCase(enchantment.getFriendlyName(), source)) {
                completions.add(enchantment.getFriendlyName().toLowerCase());
                continue;
            }
            if (!StringUtils.startsWithIgnoreCase(enchantment.getBukkitEnchantment().getName(), source)) continue;
            completions.add(enchantment.getFriendlyName().toLowerCase());
        }
        return completions;
    }
}

