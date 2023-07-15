package net.lugami.qlib.scoreboard;

import net.lugami.qlib.util.LinkedList;
import org.bukkit.entity.Player;

public interface ScoreGetter {

    void getScores(LinkedList<String> var1, Player var2);

}

