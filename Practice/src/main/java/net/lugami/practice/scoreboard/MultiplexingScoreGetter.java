package net.lugami.practice.scoreboard;

import net.lugami.qlib.scoreboard.ScoreGetter;
import net.lugami.qlib.util.LinkedList;
import net.lugami.practice.Practice;
import net.lugami.practice.match.MatchHandler;
import net.lugami.practice.setting.Setting;
import net.lugami.practice.setting.SettingHandler;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

final class MultiplexingScoreGetter implements ScoreGetter {

    private final BiConsumer<Player, LinkedList<String>> matchScoreGetter;
    private final BiConsumer<Player, LinkedList<String>> LobbyScoreGetter;

    MultiplexingScoreGetter(
            BiConsumer<Player, LinkedList<String>> matchScoreGetter,
            BiConsumer<Player, LinkedList<String>> LobbyScoreGetter
    ) {
        this.matchScoreGetter = matchScoreGetter;
        this.LobbyScoreGetter = LobbyScoreGetter;
    }

    @Override
    public void getScores(LinkedList<String> scores, Player player) {
        if (Practice.getInstance() == null) return;
        MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();

        if (settingHandler.getSetting(player, Setting.SHOW_SCOREBOARD)) {
            if (matchHandler.isPlayingOrSpectatingMatch(player)) {
                matchScoreGetter.accept(player, scores);
                } else {
                    LobbyScoreGetter.accept(player, scores);
                }
        }

        if (!scores.isEmpty()) {
            scores.addFirst("&a&7&m--------------------");
            if (Practice.getInstance().getConfig().getBoolean("scoreboard.should-add-footer")) {
                scores.add("");
                scores.add(Practice.getInstance().getDominantColor() + Practice.getInstance().getConfig().getString("networkWebsite"));
            }
            scores.add("&f&7&m--------------------");
        }
    }

}