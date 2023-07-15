package net.lugami.bridge.bukkit.commands.disguise;

import net.lugami.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.disguise.DisguisePlayer;

public class UnDisguiseCommand {

    @Command(names = {"undisguise", "undis", "ud", "unnick", "und"}, permission = "bridge.disguise", description = "Reveal yourself once again!", hidden = true)
    public static void undisguise(Player player) {
        DisguisePlayer disguisePlayer = BridgeGlobal.getDisguiseManager().getDisguisePlayers().get(player.getUniqueId());

        if (disguisePlayer == null) {
            player.sendMessage(ChatColor.RED + "You're not disguised!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Removing the disguise...");
        BridgeGlobal.getDisguiseManager().undisguise(player, true, false);
    }
}
