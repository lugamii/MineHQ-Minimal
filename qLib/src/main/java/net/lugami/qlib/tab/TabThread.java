package net.lugami.qlib.tab;

import net.lugami.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TabThread extends Thread {

    private final Plugin commonLibs = Bukkit.getServer().getPluginManager().getPlugin("CommonLibs");
    private final Plugin protocolLib = Bukkit.getServer().getPluginManager().getPlugin("ProtocolLib");

    public TabThread() {
        this.setName("qLib - Tab Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {

        while(qLib.getInstance().isEnabled() && canRun()) {
            for (Player online : qLib.getInstance().getServer().getOnlinePlayers()) {
                try {
                    FrozenTabHandler.updatePlayer(online);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(250L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean canRun() {
        if(commonLibs == null) {
            return protocolLib != null && protocolLib.isEnabled();
        }else {
            return commonLibs.isEnabled();
        }
    }
}

