package net.lugami.bridge.bukkit.commands.updater.menu;

import com.google.common.collect.Lists;
import net.lugami.qlib.menu.Button;
import net.lugami.bridge.BridgeGlobal;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.util.List;

@AllArgsConstructor
public class CompleteButton extends Button {

    private UpdaterMenu parent;
    private List<File> plugins;

    public String getName(Player player) {
        return ChatColor.GREEN + "Confirm and Update";
    }

    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
        description.add(ChatColor.BLUE + "Click to update " + ChatColor.WHITE + this.plugins.size() + " plugins" + ChatColor.BLUE + " for the groups " + ChatColor.WHITE + StringUtils.join(this.parent.getGroups(), ", ") + ChatColor.BLUE + ".");
        description.add("");
        plugins.forEach(file -> description.add(ChatColor.GRAY + " * " + ChatColor.WHITE + file.getName() + ' ' + BridgeGlobal.getUpdaterManager().getStatus(file).getPrefix()));
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 30));
        return description;
    }

    public Material getMaterial(Player player) {
        return Material.DIAMOND_SWORD;
    }

    public byte getDamageValue(Player player) {
        return 0;
    }

    public void clicked(Player player, int i, ClickType clickType) {
        this.update(this.plugins, player);
    }

    private void update(List<File> plugins, Player player) {
        if(plugins.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You must select a plugin you wish to update!");
            return;
        }

        BridgeGlobal.getUpdaterManager().updatePlugins(plugins, cons -> player.sendMessage(ChatColor.BLUE + cons));
        player.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "! YOU WILL NEED TO RESTART FOR CHANGES TO TAKE PLACE !");
        this.parent.setComplete(true);
        player.closeInventory();
    }
    
}
