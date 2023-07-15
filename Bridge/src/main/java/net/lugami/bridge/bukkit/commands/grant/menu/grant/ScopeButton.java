package net.lugami.bridge.bukkit.commands.grant.menu.grant;

import com.google.common.collect.Lists;
import net.lugami.qlib.menu.Button;
import lombok.AllArgsConstructor;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

@AllArgsConstructor
public class ScopeButton extends Button {
    private ScopesMenu parent;
    private String scope;

    public String getName(Player player) {
        boolean status = this.parent.getStatus().get(this.scope);
        return (status ? ChatColor.GREEN : ChatColor.RED) + this.scope;
    }

    public List<String> getDescription(Player player) {
        boolean status = this.parent.getStatus().get(this.scope);
        List<String> description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
        if (status) {
            description.add(ChatColor.BLUE + "Click to " + ChatColor.RED + "remove " + ChatColor.YELLOW + this.scope + ChatColor.BLUE + " from this grant's scopes.");
        }
        else {
            description.add(ChatColor.BLUE + "Click to " + ChatColor.GREEN + "add " + ChatColor.YELLOW + this.scope + ChatColor.BLUE + " to this grant's scopes.");
        }
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        boolean status = this.parent.getStatus().get(this.scope);
        return status ? DyeColor.LIME.getWoolData() : DyeColor.GRAY.getWoolData();
    }

    public void clicked(Player player, int i, ClickType clickType) {
        this.parent.getStatus().put(this.scope, !this.parent.getStatus().getOrDefault(this.scope, false));
        this.parent.setGlobal(false);
    }

}
