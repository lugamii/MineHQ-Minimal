package net.lugami.practice.commands;

import net.lugami.qlib.command.Command;
import net.lugami.practice.util.InventoryUtils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /updateInventory commands, typically only used for debugging inventory
 * issues. Available to all players to enforce the constraint that
 * {@link InventoryUtils#resetInventoryDelayed(Player)}
 * can always be called at any time.
 */
public final class UpdateInventoryCommand {

    @Command(names = {"updateinventory", "updateinv", "upinv", "ui"}, permission = "")
    public static void updateInventory(Player sender) {
        InventoryUtils.resetInventoryDelayed(sender);
        sender.sendMessage(ChatColor.GREEN + "Updated your inventory.");
    }

}