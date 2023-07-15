package net.lugami.bridge.bukkit.commands.disguise;

import net.lugami.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.disguise.DisguisePlayer;

public class UnTagCommand {

    @Command(names = {"untag"}, permission = "bridge.disguise", hidden = true)
    public static void untag(Player player) {
        DisguisePlayer disguisePlayer = BridgeGlobal.getDisguiseManager().getDisguisePlayers().get(player.getUniqueId());

        if (disguisePlayer == null) {
            player.sendMessage(ChatColor.RED + "You're not tagged!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Removing the tag...");
        BridgeGlobal.getDisguiseManager().undisguise(player, true, false);
    }
}
