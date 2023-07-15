package net.lugami.bridge.bukkit.parameters.packets.filter;

import mkremins.fanciful.FancyMessage;
import net.lugami.qlib.xpacket.XPacket;
import net.lugami.bridge.bukkit.commands.filter.ToggleFilterCommand;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class FilterViolationPacket implements XPacket {

    private String server, usernameOne, usernameTwo, message;

    @Override
    public void onReceive() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(getType(onlinePlayer) == null || getType(onlinePlayer).equalsIgnoreCase("off")) continue;
            if(getType(onlinePlayer).equals("server") && !server.equals(Bukkit.getServerName())) continue;
            FancyMessage fancyMessage = new FancyMessage("§e[Filter] ").then((getType(onlinePlayer).equals("global") ? "§7[" + server + "] " : "")).command("/server " + server).tooltip(ChatColor.GREEN +  "Click here to teleport to " + server).then("§b" + (usernameTwo == null ? usernameOne : "(" + usernameOne + " to " + usernameTwo + ")") + " §c-> §e" + message);
//            onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('§', "§e[Filter] " +  (getType(onlinePlayer).equals("global") ? "§7[" + server + "] " : "") + "§b" + (usernameTwo == null ? usernameOne : "(" + usernameOne + " to " + usernameTwo + ")") + " §c-> §e" + message));
            fancyMessage.send(onlinePlayer);
        }
    }

    private String getType(Player player) {
        if(!player.hasPermission("basic.staff")) return null;
        if(!ToggleFilterCommand.getFilter().containsKey(player.getUniqueId())) return "global";
        return ToggleFilterCommand.getFilter().get(player.getUniqueId());
    }
}
