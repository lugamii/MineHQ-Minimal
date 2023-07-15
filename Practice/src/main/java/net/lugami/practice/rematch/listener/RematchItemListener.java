package net.lugami.practice.rematch.listener;

import net.lugami.practice.duel.command.AcceptCommand;
import net.lugami.practice.duel.command.DuelCommand;
import net.lugami.practice.rematch.RematchData;
import net.lugami.practice.rematch.RematchHandler;
import net.lugami.practice.rematch.RematchItems;
import net.lugami.practice.util.InventoryUtils;
import net.lugami.practice.util.ItemListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class RematchItemListener extends ItemListener {

    public RematchItemListener(RematchHandler rematchHandler) {
        addHandler(RematchItems.REQUEST_REMATCH_ITEM, player -> {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                DuelCommand.duel(player, target, rematchData.getKitType());

                InventoryUtils.resetInventoryDelayed(player);
                InventoryUtils.resetInventoryDelayed(target);
            }
        });

        addHandler(RematchItems.SENT_REMATCH_ITEM, p -> p.sendMessage(ChatColor.RED + "You have already sent a rematch request."));

        addHandler(RematchItems.ACCEPT_REMATCH_ITEM, player -> {
            RematchData rematchData = rematchHandler.getRematchData(player);

            if (rematchData != null) {
                Player target = Bukkit.getPlayer(rematchData.getTarget());
                AcceptCommand.accept(player, target);
            }
        });
    }

}