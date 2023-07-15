package net.lugami.bridge.global.packet.types;

import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.packet.Packet;
import net.lugami.bridge.global.util.Tasks;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

@AllArgsConstructor @NoArgsConstructor
public class ServerShutdownPacket implements Packet {

    private String serverName;

    @Override
    public void onReceive() {
        if(this.serverName.equalsIgnoreCase("all") || BridgeGlobal.getSystemName().equalsIgnoreCase(this.serverName)) {
            System.out.println("Received Shutdown Packet! Closing server.");
            Tasks.run(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop"));
        } else {
            if (this.serverName.equalsIgnoreCase("all") || this.serverName.equalsIgnoreCase("BungeeCord")) {
                System.out.println("Received Shutdown Packet! Closing server.");
                Tasks.run(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "end"));
            }
        }
    }
}
