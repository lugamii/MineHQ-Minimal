package net.lugami.qlib.combatlogger;

import java.util.UUID;

import net.lugami.qlib.deathmessage.FrozenDeathMessageHandler;
import net.lugami.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;

public interface CombatLoggerConfiguration {

    CombatLoggerConfiguration DEFAULT_CONFIGURATION = user -> {
        if (FrozenDeathMessageHandler.getConfiguration() != null) {
            return FrozenDeathMessageHandler.getConfiguration().formatPlayerName(user) + ChatColor.GRAY + " (Combat-Logger)";
        }
        return ChatColor.RED + UUIDUtils.name(user) + ChatColor.GRAY + " (Combat-Logger)";
    };

    String formatPlayerName(UUID var1);

    default String formatPlayerName(UUID user, UUID formatFor) {
        return this.formatPlayerName(user);
    }

}

