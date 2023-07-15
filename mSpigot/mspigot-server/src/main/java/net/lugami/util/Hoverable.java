package net.lugami.util;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Hoverable {

    private final TextComponent textComponent = new TextComponent();

    public Hoverable(String message) {
        this.textComponent.setText(ChatColor.translateAlternateColorCodes('&', message));
    }

    public Hoverable setHoverText(String... text) {
        StringBuilder stringBuilder = new StringBuilder();

        boolean first = true;

        for (String s : text) {
            stringBuilder.append(first ? s : "\n" + s);
            first = false;
        }

        this.textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                new TextComponent(ChatColor.translateAlternateColorCodes('&', stringBuilder.toString()))
        }));

        return this;
    }

    public Hoverable setHoverText(String text) {
        this.textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{
                new TextComponent(ChatColor.translateAlternateColorCodes('&', text))
        }));

        return this;
    }

    public Hoverable send(Player player) {
        player.spigot().sendMessage(this.textComponent);
        return this;
    }

    public TextComponent build() {
        return this.textComponent;
    }
}

