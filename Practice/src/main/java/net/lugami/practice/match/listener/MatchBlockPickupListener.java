package net.lugami.practice.match.listener;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemsEvent;
import org.bukkit.inventory.ItemStack;
import net.lugami.practice.Practice;
import net.lugami.practice.match.Match;

import java.util.List;


public class MatchBlockPickupListener implements Listener {

    @EventHandler
    public void onBlockDropItems(BlockDropItemsEvent event) {
        Player recipient = event.getPlayer();
        if (recipient == null) return;

        Match match = Practice.getInstance().getMatchHandler().getMatchPlaying(recipient);
        if (match == null) return;

        if (!match.getKitType().getId().equals("SPLEEF")) return;

        List<Item> items = event.getToDrop();
        for (Item item : items) {
            ItemStack stack = item.getItemStack();
            stack.setType(Material.SNOW_BALL);
            recipient.getInventory().addItem(stack);
        }

        items.clear();
    }
}
