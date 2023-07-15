package net.lugami.qlib.scoreboard;

import com.google.common.base.Preconditions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleGetter {

    private String defaultTitle;

    @Deprecated
    public TitleGetter(String defaultTitle) {
        this.defaultTitle = ChatColor.translateAlternateColorCodes('&', defaultTitle);
    }

    public TitleGetter() {
    }

    public static TitleGetter forStaticString(final String staticString) {
        Preconditions.checkNotNull((Object)staticString);
        return new TitleGetter(){

            @Override
            public String getTitle(Player player) {
                return staticString;
            }
        };
    }

    public String getTitle(Player player) {
        return this.defaultTitle;
    }

}