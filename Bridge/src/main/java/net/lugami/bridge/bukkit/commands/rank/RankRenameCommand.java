package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.RankUpdatePacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.ranks.Rank;

public class RankRenameCommand {

    @Command(names = {"rank rename"}, permission = "bridge.rank", description = "Rename a rank", hidden = true, async = true)
    public static void RankRenameCmd(CommandSender s, @Param(name = "rank") Rank r, @Param(name = "name") String name) {
        String original = r.getName();
        r.setName(name);
        r.saveRank();
        s.sendMessage("§aSuccessfully renamed the rank from " + original + " to " + name + "!");
        PacketHandler.sendToAll(new RankUpdatePacket(r, s.getName(), BridgeGlobal.getSystemName()));
        PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.update.view", "&8[&eServer Monitor&8] &fRefreshed rank " + r.getColor() + r.getDisplayName()));
    }
}
