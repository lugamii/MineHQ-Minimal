package net.lugami.practice.util;

import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.match.MatchUtils;
import net.lugami.practice.Practice;
import net.lugami.practice.lobby.LobbyUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class InventoryUtils {

    public static final long RESET_DELAY_TICKS = 2L;

    public static void resetInventoryDelayed(Player player) {
        Runnable task = () -> resetInventoryNow(player);
        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), task, RESET_DELAY_TICKS);
    }

    public static void resetInventoryNow(Player player) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        if (matchHandler.isPlayingOrSpectatingMatch(player)) {
            MatchUtils.resetInventory(player);
        } else {
            LobbyUtils.resetInventory(player);
        }
    }

}