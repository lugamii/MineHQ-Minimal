package net.lugami.practice.tab;

import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.ranks.Rank;
import net.lugami.qlib.tab.TabLayout;
import net.lugami.qlib.util.PlayerUtils;
import net.lugami.qlib.util.UUIDUtils;
import net.lugami.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.BiConsumer;

public class AlternateOnlinePlayersLayoutProvider implements Listener, BiConsumer<Player, TabLayout> {

    private Map<UUID, String> playersMap = generateNewTreeMap();

    public AlternateOnlinePlayersLayoutProvider() {
        Bukkit.getPluginManager().registerEvents(this, Practice.getInstance());
        Bukkit.getScheduler().runTaskTimerAsynchronously(Practice.getInstance(), this::rebuildCache, 0, 1 * 60 * 20);
    }

    @Override
    public void accept(Player player, TabLayout tabLayout) {
        int x = 0;
        int y = 2;

        boolean isStaff = player.hasPermission("basic.staff");
        for (Map.Entry<UUID, String> entry : playersMap.entrySet()) {
            if (x == 3) {
                x = 0;
                y++;
            }

            if (entry.getValue() == null) {
                continue;
            }

            if (19 <= y) {
                break;
            }

            Player otherPlayer = Bukkit.getPlayer(entry.getKey());
            if (otherPlayer.hasMetadata("ModMode")) {
                if (!isStaff) {
                    continue;
                }

                tabLayout.set(x++, y, "* " + entry.getValue(), getPing(entry.getKey()));
            } else {
                tabLayout.set(x++, y, entry.getValue(), getPing(entry.getKey()));
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        playersMap.put(event.getPlayer().getUniqueId(), getName(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playersMap.remove(event.getPlayer().getUniqueId());
    }

    private void rebuildCache() {
        TreeMap<UUID, String> newTreeMap = generateNewTreeMap();

        Bukkit.getOnlinePlayers().forEach(player -> {
            newTreeMap.put(player.getUniqueId(), getName(player.getUniqueId()));
        });

        this.playersMap = newTreeMap;
    }

    private String getName(UUID uuid) {
        Profile profile = BridgeGlobal.getProfileHandler().getProfileByUUID(uuid);
        if (profile == null) {
            return UUIDUtils.name(uuid);
        }

        Rank bestDisplayRank = profile.getCurrentGrant().getRank();
        if (bestDisplayRank == null) {
            return UUIDUtils.name(uuid);
        }

        return bestDisplayRank.getColor() + UUIDUtils.name(uuid);
    }

    public int getPing(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        return player == null ? -1 : Math.max(((PlayerUtils.getPing(player) + 5) / 10) * 10, 1);
    }

    private TreeMap<UUID, String> generateNewTreeMap() {
        return new TreeMap<UUID, String>(new Comparator<UUID>() {

            @Override
            public int compare(UUID first, UUID second) {
                Profile firstProfile = BridgeGlobal.getProfileHandler().getProfileByUUID(first);
                Profile secondProfile = BridgeGlobal.getProfileHandler().getProfileByUUID(second);

                if (firstProfile != null && secondProfile != null) {
                    int compare = Integer.compare(secondProfile.getCurrentGrant().getRank().getPriority(), firstProfile.getCurrentGrant().getRank().getPriority());
                    if (compare == 0) {
                        return tieBreaker(first, second);
                    }

                    return compare;
                } else if (firstProfile != null && secondProfile == null) {
                    return -1;
                } else if (firstProfile == null && secondProfile != null) {
                    return 1;
                } else {
                    return tieBreaker(first, second);
                }
            }

        });
    }

    private int tieBreaker(UUID first, UUID second) {
        String firstName = UUIDUtils.name(first);
        String secondName = UUIDUtils.name(second);

        return firstName.compareTo(secondName);
    }

}