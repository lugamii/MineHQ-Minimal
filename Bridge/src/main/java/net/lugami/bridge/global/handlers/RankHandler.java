package net.lugami.bridge.global.handlers;

import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.ranks.Rank;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RankHandler {

    @Getter private Set<Rank> ranks = new HashSet<>();

    public void init() {
        ranks.clear();

        try {
            BridgeGlobal.getMongoHandler().getRanksInDB(callback-> {

                BridgeGlobal.sendLog("§aFound " + callback.size() + " ranks in database.");

                List<Rank> rankList = new ArrayList<>();

                AtomicInteger done = new AtomicInteger();

                Rank rank;
                for (UUID uuid : callback) {
                    rankList.add(new Rank(uuid, true, cbck -> {
                        Rank r = getRankByID(uuid);
                        if (r != null) {
                            r.load();
                            done.getAndIncrement();
                        }
                    }));
                }
                ranks.addAll(rankList);
                if (done.get() == callback.size() && getDefaultRank() == null) {
                    Rank defaultRank = new Rank(UUID.randomUUID(), "Default", false);
                    defaultRank.setDefaultRank(true);
                    defaultRank.setHidden(false);
                    defaultRank.setColor("§f");
                    ranks.add(defaultRank);
                }
            });
            BridgeGlobal.sendLog("§aSuccessfully loaded all ranks.");
        } catch (Exception ex) {
            BridgeGlobal.sendLog("§cFailed to initialize the rank manager.");
            ex.printStackTrace();
            BridgeGlobal.sendLog(ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }

    public Rank getRankByName(String name) {
        return ranks.stream().filter(rank->rank.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Rank getRankByDisplayName(String name) {
        return ranks.stream().filter(rank->rank.getDisplayName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Rank getRankByID(UUID id) {
        for (Rank rank : ranks) {
            if(rank.getUuid().equals(id)) return rank;
        }
        return null;
    }

    public Rank addRank(Rank rank) {
        Rank foundRank = ranks.stream().filter(rank1 -> rank1.getUuid().equals(rank.getUuid())).findFirst().orElse(null);;
        if(foundRank != null) ranks.remove(foundRank);
        ranks.add(rank);
        return rank;
    }

    public void save() {
        ranks.forEach(rank -> {
            BridgeGlobal.getMongoHandler().saveRank(rank, callback -> {
                if (callback) {
                    BridgeGlobal.sendLog("§aSuccessfully saved rank §f" + rank.getColor() + rank.getName() + "§a.");
                } else {
                    BridgeGlobal.sendLog("§cFailed to save rank §f" + rank.getColor() + rank.getName() + "§c.");
                }
            }, true);
        });
    }

    public void saveDisable() {
        ranks.forEach(rank -> {
            if(rank.getName().equals("")) return;
            BridgeGlobal.getMongoHandler().saveRank(rank, callback -> {
                if (callback) {
                    BridgeGlobal.sendLog("§aSuccessfully saved rank §f" + rank.getColor() + rank.getName() + "§a.");
                } else {
                    BridgeGlobal.sendLog("§cFailed to save rank §f" + rank.getColor() + rank.getName() + "§c.");
                }
            }, false);
        });
    }

    public Rank getDefaultRank() {
        return ranks.stream().filter(Rank::isDefaultRank).findFirst().orElse(null);
    }
}