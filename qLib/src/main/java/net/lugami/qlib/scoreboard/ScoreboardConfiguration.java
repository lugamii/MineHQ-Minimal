package net.lugami.qlib.scoreboard;

public final class ScoreboardConfiguration {

    private TitleGetter titleGetter;
    private ScoreGetter scoreGetter;

    public TitleGetter getTitleGetter() {
        return this.titleGetter;
    }

    public void setTitleGetter(TitleGetter titleGetter) {
        this.titleGetter = titleGetter;
    }

    public ScoreGetter getScoreGetter() {
        return this.scoreGetter;
    }

    public void setScoreGetter(ScoreGetter scoreGetter) {
        this.scoreGetter = scoreGetter;
    }
}

