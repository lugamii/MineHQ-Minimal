package net.lugami.practice.lobby.menu.statistics;

import com.google.common.collect.Lists;
import net.lugami.qlib.menu.Button;
import net.lugami.practice.Practice;
import net.lugami.practice.elo.EloHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map.Entry;

public class GlobalEloButton extends Button {

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Global" + ChatColor.GRAY + ChatColor.BOLD + " \u2503 " + ChatColor.WHITE + "Top 10";
    }

    @Override
    public List<String> getDescription(Player player) {
        EloHandler eloHandler = Practice.getInstance().getEloHandler();
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        int counter = 1;

        for (Entry<String, Integer> entry : eloHandler.topElo(null).entrySet()) {
            String color = (counter <= 3 ? ChatColor.GOLD : ChatColor.GRAY).toString();
            description.add(color + counter + ChatColor.GRAY + ChatColor.BOLD + " \u2503 " + entry.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue());

            counter++;
        }

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.NETHER_STAR;
    }
}
