package net.lugami.bridge.bukkit.commands.server.menu.server.buttons;

import lombok.AllArgsConstructor;
import net.lugami.bridge.bukkit.commands.server.menu.server.ExtraInformationMenu;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.util.BungeeUtils;
import net.lugami.qlib.util.TPSUtils;
import net.lugami.qlib.util.TimeUtil;
import net.lugami.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import net.lugami.bridge.global.status.BridgeServer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
public class ServerButton extends Button {

    private BridgeServer server;

    @Override
    public String getName(Player player) {
        return (!server.isOnline() ? ChatColor.RED : server.isWhitelisted() ? ChatColor.WHITE : ChatColor.GREEN) + server.getName();
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>();

        if(!server.isOnline()) {
            lore.add(ChatColor.RED + "This server has recently gone offline");
        }else {
            if(server.getTps() != 0.0) {
                lore.add(ChatColor.YELLOW + "TPS: " + ChatColor.WHITE + TPSUtils.formatTPS(server.getTps(), true));
            }
            lore.add(ChatColor.YELLOW + "Online: " + ChatColor.WHITE + server.getOnline() + " / " + server.getMaximum());
            lore.add(ChatColor.YELLOW + "Status: " + ChatColor.WHITE + server.formattedStatus(true));
            if(!server.getMotd().isEmpty()) {
                lore.add(ChatColor.YELLOW + "MOTD: " + ChatColor.WHITE + server.getMotd());
            }
            lore.add("");
            lore.add(ChatColor.YELLOW + "System Type: " + ChatColor.WHITE + server.getSystemType());
            lore.add(ChatColor.YELLOW + "Group: " + ChatColor.WHITE + server.getGroup());
            lore.add(ChatColor.YELLOW + "Provider: " + ChatColor.WHITE + server.getProvider());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Server Started At: " + ChatColor.WHITE + TimeUtils.formatIntoCalendarString(new Date(server.getBootTime())));
            lore.add(ChatColor.YELLOW + "Uptime: " + ChatColor.WHITE + TimeUtil.millisToTimer(System.currentTimeMillis() - server.getBootTime()));
            lore.add(ChatColor.YELLOW + "Last Updated: " + ChatColor.WHITE + TimeUtil.millisToTimer(System.currentTimeMillis() - server.getLastHeartbeat()));
            lore.add("");
            lore.add(ChatColor.GRAY + "Left click to connect.");
            lore.add((server.getMetadata() == null ? ChatColor.RED + "This server has no extra information." : ChatColor.GREEN + "Right click to for more information."));
        }

        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        if(!server.isOnline()) return Material.REDSTONE_BLOCK;
        if(server.getStatus().equals("BOOTING")) return Material.GOLD_BLOCK;
        if(server.isWhitelisted()) return Material.GOLD_BLOCK;
        if(server.isOnline()) return Material.EMERALD_BLOCK;

        return Material.REDSTONE_BLOCK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if(clickType == ClickType.LEFT) {
            player.closeInventory();
            BungeeUtils.send(player, server.getName());
        }else if(clickType == ClickType.RIGHT) {
            player.closeInventory();
            new ExtraInformationMenu(server).openMenu(player);
        }
    }
}
