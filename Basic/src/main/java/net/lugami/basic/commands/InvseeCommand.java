package net.lugami.basic.commands;

import net.lugami.basic.util.inventory.BasicPlayerInventory;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.command.parameter.offlineplayer.OfflinePlayerWrapper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InvseeCommand {

    @Command(names={"invsee"}, permission="basic.invsee", description = "Open a player's inventory")
    public static void invsee(Player sender, @Param(name="player") OfflinePlayerWrapper wrapper) {
        wrapper.loadAsync(player -> {
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "No online or offline player with the name " + wrapper.getName() + " found.");
                return;
            }
            if (player.equals(sender)) {
                sender.sendMessage(ChatColor.RED + "You can't invsee yourself!");
                return;
            }
            sender.openInventory(BasicPlayerInventory.get(player).getBukkitInventory());
            BasicPlayerInventory.getOpen().add(sender.getUniqueId());
        });
    }
}

