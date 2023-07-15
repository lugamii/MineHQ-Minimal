package net.lugami.bridge.global.handlers;

import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.util.SystemType;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Thread.sleep;

public class ProfileHandler {

    @Getter private Set<Profile> profiles = new HashSet<>();

    public void init() {
        profiles.clear();
        new Thread(() -> {

            while(true) {
                try {
                    sleep(TimeUnit.SECONDS.toMillis(10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Iterator<Profile> profileIterator = profiles.iterator();
                while (profileIterator.hasNext()) {
                    Profile profile = profileIterator.next();
                    if(!profile.isOnline()) {
                        if(BridgeGlobal.getSystemType() == SystemType.BUKKIT) profile.saveProfile();
                        profileIterator.remove();
                    }
                }
            }

        }).start();
    }

    public Profile getProfileByUUID(UUID id) {
        if (id == null) return null;
        if(id.toString().equals(Profile.getConsoleProfile().getUuid().toString())) return Profile.getConsoleProfile();
        return profiles.stream().filter(rank->rank.getUuid().toString().equalsIgnoreCase(id.toString())).findFirst().orElse(null);
    }

    public Profile getProfileByUsername(String id) {
        if (id == null) return null;
        if(id.equalsIgnoreCase("Console")) return Profile.getConsoleProfile();
        return profiles.stream().filter(rank->rank.getUsername().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public Profile getProfileByIP(String ip) {
        return profiles.stream().filter(rank-> rank.getCurrentIPAddress().equals(ip)).findFirst().orElse(null);
    }

    public Profile addProfile(Profile profile) {
        profile.refreshCurrentGrant();
        profiles.add(profile);
        return profile;
    }

    public void save() {
        profiles.forEach(profile -> {
            BridgeGlobal.getMongoHandler().saveProfile(profile, callback -> {
                if (callback) {
                    BridgeGlobal.sendLog("§aSuccessfully saved §f" + profile.getUsername() + "§a.");
                } else {
                    BridgeGlobal.sendLog("§cFailed to save §f" + profile.getUsername() + "§c.");
                }
            }, true);
        });
    }

    public void saveDisable() {
        profiles.forEach(profile -> {
            profile.setLastQuit(System.currentTimeMillis());
            BridgeGlobal.getMongoHandler().saveProfile(profile, callback -> {
                if (callback) {
                    BridgeGlobal.sendLog("§aSuccessfully saved §f" + profile.getUsername() + "§a.");
                } else {
                    BridgeGlobal.sendLog("§cFailed to save §f" + profile.getUsername() + "§c.");
                }
            }, false);
        });
    }

    public Profile getProfileByUUIDOrCreate(UUID id) {
        if(id == null) return null;
        AtomicReference<Profile> prof = new AtomicReference<>(getProfileByUUID(id));
        if (prof.get() == null) {
            try {
                BridgeGlobal.getMongoHandler().loadProfile(id.toString(), prof::set, false, MongoHandler.LoadType.UUID);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return prof.get();
    }

    public Profile getProfileByUsernameOrCreate(String id) {
        if(id == null) return null;
        AtomicReference<Profile> prof = new AtomicReference<>(getProfileByUsername(id));
        if (prof.get() == null) {
            try {
                BridgeGlobal.getMongoHandler().loadProfile(id, prof::set, false, MongoHandler.LoadType.USERNAME);
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        return prof.get();
    }

    public Profile getNewProfileOrCreate(String name, UUID op) {

        Profile prof = getProfileByUUIDOrCreate(op);
        if(prof == null) {
            BridgeGlobal.getProfileHandler().addProfile(new Profile(name, op,false)).applyGrant(Grant.getDefaultGrant(), null, false);
            prof = getProfileByUUID(op);
        }
        return prof;
    }

}
