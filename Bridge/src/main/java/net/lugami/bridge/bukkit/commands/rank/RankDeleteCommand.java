package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.RankDeletePacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.ranks.Rank;

public class RankDeleteCommand {

    @Command(names = {"rank delete", "rank remove"}, permission = "bridge.rank", description = "Delete a rank", hidden = true, async = true)
    public static void RankDeleteCmd(CommandSender s, @Param(name = "rank") Rank r) {
        s.sendMessage("§aSuccessfully deleted the rank " + r.getColor() + r.getName() + "§a!");
        BridgeGlobal.getMongoHandler().removeRank(r.getUuid(), callback -> {
        }, true);
        PacketHandler.sendToAll(new RankDeletePacket(r, s.getName(), BridgeGlobal.getServerName()));
        PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.update.view", "&8[&eServer Monitor&8] &fDeleted rank " + r.getColor() + r.getDisplayName()));
    }
}
