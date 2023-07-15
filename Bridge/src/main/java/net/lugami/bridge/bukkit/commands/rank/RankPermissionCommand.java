package net.lugami.bridge.bukkit.commands.rank;

import net.lugami.bridge.global.packet.types.RankUpdatePacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.command.CommandSender;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.ranks.Rank;

public class RankPermissionCommand {

    @Command(names = {"rank permission", "rank perm"}, permission = "bridge.rank", description = "Add/remove a permission", hidden = true, async = true)
    public static void RankPermissionCmd(CommandSender s, @Param(name = "rank") Rank r, @Param(name = "permission") String perm, @Param(name = "group", defaultValue = "§") String serverGroup) {
        String group = serverGroup.equals("§") ? "Global" : serverGroup;
        boolean b = r.togglePerm(perm, group);
        r.saveRank();
        s.sendMessage("§aSuccessfully " + (b ? "added" : "removed") + " the permission " + perm + " to the scope: " + group);
        PacketHandler.sendToAll(new RankUpdatePacket(r, s.getName(), BridgeGlobal.getSystemName()));
    }
}
