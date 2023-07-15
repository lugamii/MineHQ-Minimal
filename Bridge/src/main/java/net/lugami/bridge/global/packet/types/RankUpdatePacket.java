package net.lugami.bridge.global.packet.types;

import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.Packet;
import net.lugami.bridge.global.ranks.Rank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.lugami.bridge.bukkit.listener.GeneralListener;

@AllArgsConstructor @NoArgsConstructor
public class RankUpdatePacket implements Packet {

    private Rank rank;
    private String creator, server;

    @Override
    public void onReceive() {
        if(server.equals(BridgeGlobal.getSystemName())) {
            rank.getOnlineProfilesInRank().forEach(profile -> GeneralListener.updatePermissions(profile.getUuid()));
            return;
        }
        BridgeGlobal.getRankHandler().addRank(rank);
        BridgeGlobal.sendLog("Refreshed rank " + rank.getDisplayName() + " (Executed by " + creator + " on " + server + ")");
        rank.getOnlineProfilesInRank().forEach(profile -> GeneralListener.updatePermissions(profile.getUuid()));

    }

}
