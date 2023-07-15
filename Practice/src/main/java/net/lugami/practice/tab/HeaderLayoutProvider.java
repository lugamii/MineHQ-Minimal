package net.lugami.practice.tab;

import net.lugami.practice.match.MatchHandler;
import net.lugami.qlib.tab.TabLayout;
import net.lugami.qlib.util.PlayerUtils;
import net.lugami.practice.Practice;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

final class HeaderLayoutProvider implements BiConsumer<Player, TabLayout> {

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();

        header: {
            tabLayout.set(1, 0, ChatColor.GOLD.toString() + ChatColor.BOLD + "Practice");
        }

        status: {
            tabLayout.set(0, 1, ChatColor.GRAY + "Online: " + Bukkit.getOnlinePlayers().size());
            tabLayout.set(1, 1, ChatColor.GRAY + "Your Connection", Math.max(((PlayerUtils.getPing(player) + 5) / 10) * 10, 1));
            tabLayout.set(2, 1, ChatColor.GRAY + "In Fights: " + matchHandler.countPlayersPlayingInProgressMatches());
        }
    }

}