package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.RankUpdatePacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.ranks.Rank;

public class RankSetColorCommand {
    @Command(names = {"rank setcolor", "rank setcolour"}, permission = "bridge.rank", description = "Set a ranks color", hidden = true, async = true)
    public static void RankSetColorCmd(CommandSender s, @Param(name = "rank") Rank r, @Param(name = "color") String col) {
        String color = ChatColor.translateAlternateColorCodes('&', col);
        r.setColor(color);
        r.saveRank();
        s.sendMessage("§aSuccessfully changed the color to " + color + r.getName() + "§a!");
        PacketHandler.sendToAll(new RankUpdatePacket(r, s.getName(), BridgeGlobal.getSystemName()));
        PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.update.view", "&8[&eServer Monitor&8] &fRefreshed rank " + r.getColor() + r.getDisplayName()));
    }
}
