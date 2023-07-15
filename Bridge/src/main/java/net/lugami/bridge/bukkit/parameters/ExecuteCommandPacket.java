package net.lugami.bridge.bukkit.parameters;

import lombok.AllArgsConstructor;
import net.lugami.qlib.xpacket.XPacket;
import org.bukkit.Bukkit;

@AllArgsConstructor
public class ExecuteCommandPacket implements XPacket {

    public String command;

    @Override
    public void onReceive() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
