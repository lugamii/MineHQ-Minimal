package net.lugami.bridge.bukkit.commands.grant.menu.grant;

import com.google.common.collect.ImmutableList;
import net.lugami.qlib.menu.Button;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class GlobalButton extends Button {

    private ScopesMenu parent;

    public String getName(Player player) {
        return ChatColor.BLUE + "Global";
    }

    public List<String> getDescription(Player player) {
        return ImmutableList.of();
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        return this.parent.isGlobal() ? DyeColor.LIME.getWoolData() : DyeColor.GRAY.getWoolData();
    }

    public void clicked(Player player, int i, ClickType clickType) {
        for (String key : this.parent.getStatus().keySet()) {
            this.parent.getStatus().put(key, false);
        }
        this.parent.setGlobal(!this.parent.isGlobal());
    }

    public GlobalButton(ScopesMenu parent) {
        this.parent = parent;
    }
}
