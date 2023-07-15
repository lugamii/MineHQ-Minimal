package net.lugami.qlib.nametag;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.*;

final class NametagThread extends Thread
{
    @Getter private static final Map<NametagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

    public NametagThread() {
        super("qLib - Nametag Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            Iterator<NametagUpdate> pendingUpdatesIterator = NametagThread.pendingUpdates.keySet().iterator();
            while (pendingUpdatesIterator.hasNext()) {
                NametagUpdate pendingUpdate = pendingUpdatesIterator.next();
                try {
                    FrozenNametagHandler.applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(FrozenNametagHandler.getUpdateInterval() * 50L);
            }
            catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

}