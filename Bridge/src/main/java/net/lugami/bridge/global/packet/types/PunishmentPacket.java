package net.lugami.bridge.global.packet.types;

import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.Packet;
import net.lugami.bridge.global.util.SystemType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.lugami.bridge.bukkit.listener.GeneralListener;
import net.lugami.bridge.global.punishment.Punishment;

@AllArgsConstructor @NoArgsConstructor
public class PunishmentPacket implements Packet {

    private Punishment punishment;

    @Override
    public void onReceive() {
        if(BridgeGlobal.getSystemType() == SystemType.BUKKIT) GeneralListener.handlePunishment(punishment, punishment.isPardoned());
    }

}
