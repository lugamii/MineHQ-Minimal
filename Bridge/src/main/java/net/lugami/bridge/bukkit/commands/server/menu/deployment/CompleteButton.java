package net.lugami.bridge.bukkit.commands.server.menu.deployment;

import com.google.common.collect.Lists;
import net.lugami.qlib.menu.Button;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.ServerDeployPacket;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CompleteButton
extends Button {
    private DeploymentMenu parent;
    private List<File> plugins;

    public String getName(Player player) {
        return ChatColor.GREEN + "Confirm and Update";
    }

    public List<String> getDescription(Player player) {
        ArrayList description = Lists.newArrayList();
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', (int)30));
        description.add(ChatColor.BLUE + "Click to deploy " + ChatColor.WHITE + this.plugins.size() + " plugins" + ChatColor.BLUE + " to the server " + ChatColor.WHITE + this.parent.getServerName() + ChatColor.BLUE + ".");
        description.add("");
        this.plugins.forEach(file -> description.add(ChatColor.GRAY + " * " + ChatColor.WHITE + file.getName() + ' ' + BridgeGlobal.getUpdaterManager().getStatus((File)file).getPrefix()));
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', (int)30));
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
        if (plugins.isEmpty()) {
            player.sendMessage(ChatColor.RED + "You must select a plugin you wish to deploy!");
            return;
        }
        PacketHandler.sendToAll(new ServerDeployPacket(parent.getServerName(), plugins));
        player.sendMessage(ChatColor.GREEN + "Attempting to deploy a server with name \"" + parent.getServerName() + "\" now.");
        this.parent.setComplete(true);
        PacketHandler.sendToAll(new NetworkBroadcastPacket("basic.staff", "&8[&eServer Monitor&8] &fAdding server " + parent.getServerName() + "..."));
        player.closeInventory();
    }

    public CompleteButton(DeploymentMenu parent, List<File> plugins) {
        this.parent = parent;
        this.plugins = plugins;
    }
}

