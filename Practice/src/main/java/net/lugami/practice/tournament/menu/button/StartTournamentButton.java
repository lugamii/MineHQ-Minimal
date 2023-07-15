package net.lugami.practice.tournament.menu.button;

import net.lugami.qlib.menu.Button;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.tournament.TournamentHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class StartTournamentButton extends Button {

    private final KitType kitType;
    private int size = 1;
    private int size1 = 30;

    public StartTournamentButton(KitType kitType, int size, int size1) {
        this.size = size;
        this.size1 = size1;
        this.kitType = kitType;
    }

    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Confirm and Start";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();
            description.add(ChatColor.GREEN + "Click here to start the tournament");
            description.add(" ");
            description.add(ChatColor.YELLOW + "KitType: " + ChatColor.LIGHT_PURPLE + kitType.getDisplayName());
            description.add(ChatColor.YELLOW + "Required Teams: " + ChatColor.LIGHT_PURPLE + size1);
            description.add(ChatColor.YELLOW + "Required Team Size: " + ChatColor.LIGHT_PURPLE + size);

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.STAINED_GLASS_PANE;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 5;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.closeInventory();
        TournamentHandler.tournamentCreate(player, kitType, size, size1);
    }

}