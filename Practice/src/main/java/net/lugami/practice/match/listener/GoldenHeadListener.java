package net.lugami.practice.match.listener;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class GoldenHeadListener implements Listener {

    private static final int HEALING_POINTS = 8; // half hearts, so 4 hearts
    private static final ItemStack GOLDEN_HEAD = ItemBuilder.of(Material.GOLDEN_APPLE)
            .name("&6&lGolden Head")
            .build();

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        if (matches(item)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, HEALING_POINTS * 25, 1), true);
        }
    }

    private boolean matches(ItemStack item) {
        return GOLDEN_HEAD.isSimilar(item);
    }

    @Command(names = "givemehead", permission = "op")
    public static void giveMeHead(Player sender, @Param(name = "integer") int amount) {

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED.toString() + amount + " is less than one!");
            return;
        }

        ItemStack heads = GOLDEN_HEAD.clone();
        heads.setAmount(amount);

        sender.getInventory().addItem(heads);
        sender.updateInventory();

        sender.sendMessage(ChatColor.GREEN + "Successfully given " + amount + " Golden Head" + (amount != 1 ? "s" : "") + "!");
        return;
    }
}