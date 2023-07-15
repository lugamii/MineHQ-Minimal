package net.lugami.practice.tournament.menu.button;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.tournament.menu.TournamentParameterMenu;

import java.util.ArrayList;
import java.util.List;

public class TournamentButton extends Button {

    private final KitType kitType;
    private final Callback<KitType> callback;
    private final List<String> descriptionLines;
    private final int amount;

    public TournamentButton(KitType kitType, Callback<KitType> callback) {
        this(kitType, callback, ImmutableList.of(), 1);
    }

    TournamentButton(KitType kitType, Callback<KitType> callback, List<String> descriptionLines, int amount) {
        this.kitType = Preconditions.checkNotNull(kitType, "kitType");
        this.callback = Preconditions.checkNotNull(callback, "callback");
        this.descriptionLines = ImmutableList.copyOf(descriptionLines);
        this.amount = amount;
    }

    @Override
    public String getName(Player player) {
        return kitType.getDisplayColor() + kitType.getDisplayName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        if (kitType.isHidden()) {
            description.add(ChatColor.GRAY + "Hidden from normal players");
        }

        if (!descriptionLines.isEmpty()) {
            if (!(description.isEmpty())) {
                description.add("");
            }
            description.addAll(descriptionLines);
        }

        description.add("");
        description.add(ChatColor.GOLD + "Click here to select " + kitType.getDisplayName() + " as the kit for the " + ChatColor.GOLD + ChatColor.BOLD + "tournament" + ChatColor.GOLD + ".");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return kitType.getIcon().getItemType();
    }

    @Override
    public int getAmount(Player player) {
        return amount;
    }

    @Override
    public byte getDamageValue(Player player) {
        return kitType.getIcon().getData();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        new TournamentParameterMenu(kitType, ChatColor.BLUE.toString() + ChatColor.BOLD + "Select a tournament settings").openMenu(player);
    }

}