package net.lugami.bridge.bukkit.parameters.packets.filter;

import net.lugami.qlib.xpacket.XPacket;
import net.lugami.bridge.BridgeGlobal;
import org.bukkit.Bukkit;
import net.lugami.bridge.global.filter.Filter;

public class FilterDeletePacket implements XPacket {

    private String filter;
    private String sender;

    public FilterDeletePacket(Filter filter, String sender) {
        this.filter = filter.getPattern();
        this.sender = sender;
    }

    @Override
    public void onReceive() {
        if(sender.equalsIgnoreCase(Bukkit.getServerName())) return;
        BridgeGlobal.getFilterHandler().removeFilter(BridgeGlobal.getFilterHandler().getFilter(filter));
    }
}
