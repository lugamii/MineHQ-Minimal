package net.lugami.bridge.bukkit.commands.rank.menu.buttons;

import lombok.AllArgsConstructor;
import net.lugami.qlib.menu.Button;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.packet.types.RankUpdatePacket;
import net.lugami.bridge.global.ranks.Rank;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class PermissionButton extends Button {

    private Rank rank;
    private String str;

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW + str;
    }

    @Override
    public List<String> getDescription(Player player) {
        return Arrays.asList(
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 34),
                ChatColor.YELLOW + "Scope: " + ChatColor.RED + rank.getPermissions().get(str),
                "",
                ChatColor.RED.toString() + ChatColor.BOLD + "Click to remove",
                ChatColor.RED.toString() + ChatColor.BOLD + "this permission",
                ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 34));
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PAPER;
    }

    @Override
    public byte getDamageValue(Player var1) {
        return 0;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        player.sendMessage(ChatColor.YELLOW + "You have removed the permission " + ChatColor.RED + str + ChatColor.YELLOW + ".") ;
        rank.togglePerm(str, rank.getPermissions().get(str));
        rank.saveRank();
        PacketHandler.sendToAll(new RankUpdatePacket(rank, player.getName(), BridgeGlobal.getSystemName()));
    }
}