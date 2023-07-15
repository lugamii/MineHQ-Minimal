package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.RankCreatePacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.BukkitAPI;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.ranks.Rank;

public class RankCreateCommand {

    @Command(names = {"rank create"}, permission = "bridge.rank", description = "Create a rank", hidden = true, async = true)
    public static void RankCreateCmd(CommandSender s, @Param(name = "rank") String name) {
        Rank r = BukkitAPI.getRank(name);
        if (r != null) {
            s.sendMessage("§cThere is already a rank with the name \"" + r.getName() + "\".");
            return;
        }
        r = BukkitAPI.createRank(name);
        PacketHandler.sendToAll(new RankCreatePacket(r, s.getName(), BridgeGlobal.getSystemName()));
        PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.update.view", "&8[&eServer Monitor&8] &fCreated rank " + r.getColor() + r.getDisplayName()));
        s.sendMessage("§aSuccessfully created the rank " + r.getColor() + r.getName() + "§a!");
    }
}
