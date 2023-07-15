package net.lugami.qlib.scoreboard;

import com.google.common.base.Preconditions;
import net.lugami.qlib.qLib;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FrozenScoreboardHandler {

    private static final Map<UUID, FrozenScoreboard> boards = new ConcurrentHashMap<>();
    private static ScoreboardConfiguration configuration = null;
    private static boolean initiated = false;
    private static int updateInterval = 2;

    private FrozenScoreboardHandler() {
    }

    public static void init() {
        if (qLib.getInstance().getConfig().getBoolean("disableScoreboard", false)) {
            return;
        }
        Preconditions.checkState(!initiated);
        initiated = true;
        new ScoreboardThread().start();
        qLib.getInstance().getServer().getPluginManager().registerEvents(new ScoreboardListener(), qLib.getInstance());
    }

    protected static void create(Player player) {
        if (configuration != null) {
            boards.put(player.getUniqueId(), new FrozenScoreboard(player.getUniqueId()));
        }
    }

    protected static void updateScoreboard(Player player) {
        FrozenScoreboard board = boards.get(player.getUniqueId());
        if (board != null) {
            board.update();
        }
    }

    protected static void remove(Player player) {
        boards.remove(player.getUniqueId());
    }

    public static ScoreboardConfiguration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(ScoreboardConfiguration configuration) {
        FrozenScoreboardHandler.configuration = configuration;
    }

    public static int getUpdateInterval() {
        return updateInterval;
    }

    public static void setUpdateInterval(int updateInterval) {
        FrozenScoreboardHandler.updateInterval = updateInterval;
    }
}

