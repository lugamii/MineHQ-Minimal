package net.lugami.qlib.nametag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter @AllArgsConstructor
public abstract class NametagProvider {

    private final String name;
    private final int weight;

    public abstract NametagInfo fetchNametag(Player var1, Player var2);

    public static NametagInfo createNametag(String prefix, String suffix) {
        return FrozenNametagHandler.getOrCreate(prefix, suffix);
    }

    protected static final class DefaultNametagProvider
    extends NametagProvider {
        public DefaultNametagProvider() {
            super("Default Provider", 0);
        }

        @Override
        public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
            return DefaultNametagProvider.createNametag("", "");
        }
    }

}

