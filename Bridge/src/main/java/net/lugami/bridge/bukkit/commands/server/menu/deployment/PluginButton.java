package net.lugami.bridge.bukkit.commands.server.menu.deployment;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.lugami.qlib.menu.Button;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import net.lugami.bridge.BridgeGlobal;

import java.io.File;
import java.util.List;

@AllArgsConstructor
public class PluginButton extends Button {

    private DeploymentMenu parent;
    private File plugin;

    public String getName(Player player) {
        boolean status = this.parent.getStatus().get(this.plugin);
        return (status ? ChatColor.GREEN : ChatColor.RED) + this.plugin.getName() + " " + BridgeGlobal.getUpdaterManager().getStatus(plugin).getPrefix();
    }

    public List<String> getDescription(Player player) {
        boolean status = this.parent.getStatus().get(this.plugin);
        List<String> description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
        if (status) {
            description.add(ChatColor.BLUE + "Click to " + ChatColor.RED + "remove " + ChatColor.YELLOW + this.plugin.getName() + ChatColor.BLUE + " from the deployment list.");
        }
        else {
            description.add(ChatColor.BLUE + "Click to " + ChatColor.GREEN + "add " + ChatColor.YELLOW + this.plugin.getName() + ChatColor.BLUE + " to the deployment list.");
        }
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.WOOL;
    }

    public byte getDamageValue(Player player) {
        boolean status = this.parent.getStatus().get(this.plugin);
        return status ? DyeColor.LIME.getWoolData() : DyeColor.GRAY.getWoolData();
    }

    public void clicked(Player player, int i, ClickType clickType) {
        this.parent.getStatus().put(this.plugin, !this.parent.getStatus().getOrDefault(this.plugin, false));
    }

}

