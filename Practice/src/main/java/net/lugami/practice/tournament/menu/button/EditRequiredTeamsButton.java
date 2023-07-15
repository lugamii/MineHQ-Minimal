package net.lugami.practice.tournament.menu.button;

import com.google.common.collect.ImmutableList;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.util.Callback;
import net.lugami.practice.Practice;
import net.lugami.practice.tournament.Tournament;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class EditRequiredTeamsButton extends Button {
    private int size1;
    private Callback<Integer> callback;

    public EditRequiredTeamsButton(int size1, Callback<Integer> callback) {
        this.size1 = size1;
        this.callback = callback;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Edit Required Teams";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
                ChatColor.YELLOW + "Current: " + ChatColor.WHITE + size1,
                "",
                ChatColor.GREEN.toString() + ChatColor.BOLD + "LEFT-CLICK " + ChatColor.GREEN + "to increase by 1",
                ChatColor.GREEN.toString() + ChatColor.BOLD + "SHIFT LEFT-CLICK " + ChatColor.GREEN + "to increase by 10",
                "",
                ChatColor.RED.toString() + ChatColor.BOLD + "RIGHT-CLICK " + ChatColor.GREEN + "to decrease by 1",
                ChatColor.RED.toString() + ChatColor.BOLD + "SHIFT RIGHT-CLICK " + ChatColor.GREEN + "to decrease by 10"
        );
    }
//        List<String> description = new ArrayList<>();
//        description.add(ChatColor.GOLD + "Current: " + ChatColor.WHITE +  size1);
//        description.add(" ");
//        if (size1 == 30) {
//            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "30");
//            description.add(ChatColor.GRAY + " 40");
//            description.add(ChatColor.GRAY + " 50");
//            description.add(ChatColor.GRAY + " 100");
//            description.add(ChatColor.GRAY + " 128");
//        } else if (size1 == 40) {
//            description.add(ChatColor.GRAY + " 30");
//            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "40");
//            description.add(ChatColor.GRAY + " 50");
//            description.add(ChatColor.GRAY + " 100");
//            description.add(ChatColor.GRAY + " 128");
//        } else if (size1 == 50) {
//            description.add(ChatColor.GRAY + " 30");
//            description.add(ChatColor.GRAY + " 40");
//            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "50");
//            description.add(ChatColor.GRAY + " 100");
//            description.add(ChatColor.GRAY + " 128");
//        } else if (size1 == 100) {
//            description.add(ChatColor.GRAY + " 30");
//            description.add(ChatColor.GRAY + " 40");
//            description.add(ChatColor.GRAY + " 50");
//            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "100");
//            description.add(ChatColor.GRAY + " 128");
//        } else if (size1 == 128) {
//            description.add(ChatColor.GRAY + " 30");
//            description.add(ChatColor.GRAY + " 40");
//            description.add(ChatColor.GRAY + " 50");
//            description.add(ChatColor.GRAY + " 100");
//            description.add(ChatColor.GOLD + " ► " + ChatColor.GREEN + "128");
//
//        }
//        return description;
//    }

    @Override
    public Material getMaterial(Player player) {
        return Material.GHAST_TEAR;
    }

    @Override
    public int getAmount(Player player) {
        return size1;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Tournament tournament = Practice.getInstance().getTournamentHandler().getTournament();
        int existing = tournament.countTeamSize(tournament);
        int create = clickType.isShiftClick() ? 10 : 1;
        int desired = existing + create;
                player.sendMessage(ChatColor.GREEN + "Scaled Required Players trait to " + desired + ".");
                callback.callback(desired);
    }
}