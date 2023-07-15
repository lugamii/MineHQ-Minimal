package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.RankUpdatePacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.ranks.Rank;

public class RankSetGrantableCommand {

    @Command(names = {"rank setgrantable"}, permission = "bridge.rank", description = "Set a rank grantable status", hidden = true, async = true)
    public static void RankSetGrantableCmd(CommandSender s, @Param(name = "rank") Rank r, @Param(name = "grantable") boolean grantable) {
        r.setGrantable(grantable);
        r.saveRank();
        s.sendMessage("§aSuccessfully changed the grantable status to " + grantable + "§a!");
        PacketHandler.sendToAll(new RankUpdatePacket(r, s.getName(), BridgeGlobal.getSystemName()));
        PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.update.view", "&8[&eServer Monitor&8] &fRefreshed rank " + r.getColor() + r.getDisplayName()));
    }
}
