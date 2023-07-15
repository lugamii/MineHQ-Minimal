package net.lugami.qlib.menu.buttons;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

import net.lugami.qlib.menu.Button;
import net.lugami.qlib.util.Callback;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BooleanButton extends Button {

    private final boolean confirm;
    private final Callback<Boolean> callback;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (this.confirm) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0f, 0.1f);
        } else {
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20.0f, 0.1f);
        }
        player.closeInventory();
        this.callback.callback(this.confirm);
    }

    @Override
    public String getName(Player player) {
        return this.confirm ? "\u00a7aConfirm" : "\u00a7cCancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return this.confirm ? (byte)5 : 14;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    @ConstructorProperties(value={"confirm", "callback"})
    public BooleanButton(boolean confirm, Callback<Boolean> callback) {
        this.confirm = confirm;
        this.callback = callback;
    }
}

