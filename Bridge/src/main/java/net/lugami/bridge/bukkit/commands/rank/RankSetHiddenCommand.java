package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.RankUpdatePacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.ranks.Rank;

public class RankSetHiddenCommand {

    @Command(names = {"rank sethidden"}, permission = "bridge.rank", description = "Set a rank hidden status", hidden = true, async = true)
    public static void RankSetHiddenCmd(CommandSender s, @Param(name = "rank") Rank r, @Param(name = "hidden") boolean hidden) {
        r.setHidden(hidden);
        r.saveRank();
        s.sendMessage("§aSuccessfully changed the hidden status to " + hidden + "§a!");
        PacketHandler.sendToAll(new RankUpdatePacket(r, s.getName(), BridgeGlobal.getSystemName()));
        PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.update.view", "&8[&eServer Monitor&8] &fRefreshed rank " + r.getColor() + r.getDisplayName()));
    }
}
