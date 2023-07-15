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

public class RankSetSuffixCommand {
    @Command(names = {"rank setsuffix"}, permission = "bridge.rank", description = "Set a ranks suffix", hidden = true, async = true)
    public static void RankSetSuffixCmd(CommandSender s, @Param(name = "rank") Rank r, @Param(name = "suffix", wildcard = true) String sfx) {
        String suffix = ChatColor.translateAlternateColorCodes('&', sfx);
        r.setSuffix(suffix);
        r.saveRank();
        s.sendMessage("§aSuccessfully changed the suffix to " + sfx + "§a!");
        PacketHandler.sendToAll(new RankUpdatePacket(r, s.getName(), BridgeGlobal.getSystemName()));
        PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.update.view", "&8[&eServer Monitor&8] &fRefreshed rank " + r.getColor() + r.getDisplayName()));
    }
}
