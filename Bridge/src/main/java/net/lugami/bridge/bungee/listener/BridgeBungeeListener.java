package net.lugami.bridge.bungee.listener;

import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.global.handlers.MongoHandler;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.BridgeGlobal;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class BridgeBungeeListener implements Listener {

    @EventHandler
    public void onLogin(PostLoginEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        if (BridgeGlobal.getProfileHandler().getProfileByUUID(uuid) == null) {
            BridgeGlobal.getMongoHandler().loadProfile(uuid.toString(), callback -> {
                if (callback == null) {
                    BridgeGlobal.getProfileHandler().addProfile(new Profile(e.getPlayer().getName(), uuid, false)).applyGrant(Grant.getDefaultGrant(), null, false);
                } else {
                    BridgeGlobal.getProfileHandler().addProfile(callback);
                }
            }, true, MongoHandler.LoadType.UUID);
        } else {
            BridgeGlobal.getProfileHandler().getProfileByUUID(uuid).refreshCurrentGrant();
        }
    }

    @EventHandler(priority = 100)
    public void onJoin(LoginEvent e) {
        UUID uuid = e.getConnection().getUniqueId();
        Profile profile = BridgeGlobal.getProfileHandler().getProfileByUUID(e.getConnection().getUniqueId());
        if (profile != null) {
            profile.refreshCurrentGrant();

        }else {
            BridgeGlobal.getMongoHandler().loadProfile(uuid.toString(), callback -> {
                if (callback == null) {
                    BridgeGlobal.getProfileHandler().addProfile(new Profile(e.getConnection().getName(), uuid, false)).applyGrant(Grant.getDefaultGrant(), null, false);
                } else {
                    BridgeGlobal.getProfileHandler().addProfile(callback);
                }
            }, true, MongoHandler.LoadType.UUID);
        }

    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        Profile profile = BridgeGlobal.getProfileHandler().getProfileByUUID(e.getPlayer().getUniqueId());
        if (profile != null) {
            BridgeGlobal.getProfileHandler().getProfiles().remove(profile);
        }
    }
}
