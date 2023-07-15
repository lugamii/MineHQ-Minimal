package net.lugami.basic.listener;

import org.bukkit.ChatColor;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class HeadNameListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getState() instanceof Skull) {
            Skull skull = (Skull)block.getState();
            if (skull.getSkullType() != SkullType.PLAYER) {
                return;
            }
            String owner = skull.getOwner() == null ? "Steve" : skull.getOwner();
            player.sendMessage(ChatColor.YELLOW + "This is the head of: " + owner);
        }
    }
}

