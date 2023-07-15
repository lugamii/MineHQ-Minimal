package net.lugami.bridge.bukkit.parameters.packets.filter;

import net.lugami.qlib.xpacket.XPacket;
import net.lugami.bridge.BridgeGlobal;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import net.lugami.bridge.global.filter.Filter;

@AllArgsConstructor
public class FilterCreatePacket implements XPacket {

    private Filter filter;
    private String sender;

    @Override
    public void onReceive() {
        if(sender.equalsIgnoreCase(Bukkit.getServerName())) return;
        BridgeGlobal.getFilterHandler().addFilter(filter);
    }
}
