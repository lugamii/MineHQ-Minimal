package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.RankUpdatePacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.ranks.Rank;

public class RankSetBuilderCommand {

    @Command(names = {"rank setbuilder"}, permission = "bridge.rank", description = "Set a rank staff status", hidden = true, async = true)
    public static void RankSetBuilderCmd(CommandSender s, @Param(name = "rank") Rank r, @Param(name = "builder") boolean builder) {
        r.setBuilder(builder);
        r.saveRank();
        s.sendMessage("§aSuccessfully changed the builder status to " + builder + "§a!");
        PacketHandler.sendToAll(new RankUpdatePacket(r, s.getName(), BridgeGlobal.getSystemName()));
        PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.update.view", "&8[&eServer Monitor&8] &fRefreshed rank " + r.getColor() + r.getDisplayName()));
    }
}
