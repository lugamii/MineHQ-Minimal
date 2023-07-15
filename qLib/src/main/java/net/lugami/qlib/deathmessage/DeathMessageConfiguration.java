package net.lugami.qlib.deathmessage;

import net.lugami.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;

import java.util.UUID;

public interface DeathMessageConfiguration {

    DeathMessageConfiguration DEFAULT_CONFIGURATION = new DeathMessageConfiguration(){

        @Override
        public boolean shouldShowDeathMessage(UUID checkFor, UUID died, UUID killer) {
            return true;
        }

        @Override
        public String formatPlayerName(UUID player) {
            return ChatColor.RED + UUIDUtils.name(player);
        }
    };

    boolean shouldShowDeathMessage(UUID var1, UUID var2, UUID var3);

    String formatPlayerName(UUID var1);

    default String formatPlayerName(UUID player, UUID formatFor) {
        return this.formatPlayerName(player);
    }

    default boolean hideWeapons() {
        return false;
    }

}

