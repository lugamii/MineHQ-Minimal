package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MoreCommand {

    @Command(names={"more"}, permission="basic.give", description = "Give yourself more of the item you're holding")
    public static void more(Player sender, @Param(name="amount", defaultValue="42069420") int amount) {
        if (sender.getItemInHand() == null) {
            sender.sendMessage(ChatColor.RED + "You must be holding an item.");
            return;
        }
        if (amount == 42069420) {
            sender.getItemInHand().setAmount(64);
        } else {
            sender.getItemInHand().setAmount(Math.min(64, sender.getItemInHand().getAmount() + amount));
        }
        sender.sendMessage(ChatColor.GOLD + "There you go.");
    }
}

