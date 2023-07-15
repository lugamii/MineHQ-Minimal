package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.RankUpdatePacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.ranks.Rank;

public class RankInheritCommand {

    @Command(names = {"rank inherit"}, permission = "bridge.rank", description = "Add/remove a rank inherit", hidden = true, async = true)
    public static void RankPermissionCmd(CommandSender s, @Param(name = "rank") Rank r, @Param(name = "inherit") Rank inhr) {
        boolean b = r.toggleInherit(inhr);
        r.saveRank();
        s.sendMessage("§aSuccessfully " + (b ? "added" : "removed") + " the inherit of " + inhr.getColor() + inhr.getDisplayName());
        PacketHandler.sendToAll(new RankUpdatePacket(r, s.getName(), BridgeGlobal.getSystemName()));
        PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.update.view", "&8[&eServer Monitor&8] &fRefreshed rank " + r.getColor() + r.getDisplayName()));
    }
}
