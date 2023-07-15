package net.lugami.qlib.scoreboard;

import net.lugami.qlib.util.TimeUtils;

public interface ScoreFunction<T> {

    ScoreFunction<Float> TIME_FANCY = value -> {
        if (value >= 60.0f) {
            return TimeUtils.formatIntoMMSS(value.intValue());
        }
        return (double)Math.round(10.0 * (double) value) / 10.0 + "s";
    };

    ScoreFunction<Float> TIME_SIMPLE = value -> TimeUtils.formatIntoMMSS(value.intValue());

    ScoreFunction<Float> TIME_SCOREBOARD = value -> TimeUtils.formatScoreboardHHMMSS(value.intValue());

    String apply(T var1);
}

