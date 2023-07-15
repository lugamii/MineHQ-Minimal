package net.lugami.practice.lobby.menu.statistics;

import com.google.common.collect.Lists;
import net.lugami.qlib.menu.Button;
import net.lugami.practice.Practice;
import net.lugami.practice.elo.EloHandler;
import net.lugami.practice.kittype.KitType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map.Entry;

public class KitButton extends Button {

    private KitType kitType;

    public KitButton(KitType kitType) {
        this.kitType = kitType;
    }

    @Override
    public String getName(Player player) {
        return kitType.getColoredDisplayName() + ChatColor.GRAY + ChatColor.BOLD + " \u2503 " + ChatColor.WHITE + "Top 10";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();
        EloHandler eloHandler = Practice.getInstance().getEloHandler();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        int counter = 1;

        for (Entry<String, Integer> entry : eloHandler.topElo(kitType).entrySet()) {
            String color = (counter <= 3 ? ChatColor.GOLD : ChatColor.GRAY).toString();
            description.add(color + counter + ChatColor.GRAY + ChatColor.BOLD + " \u2503 " + entry.getKey() + ChatColor.GRAY + ": " + ChatColor.WHITE + entry.getValue());

            counter++;
        }

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return kitType.getIcon().getItemType();
    }

    @Override
    public byte getDamageValue(Player player) {
        return kitType.getIcon().getData();
    }
}
