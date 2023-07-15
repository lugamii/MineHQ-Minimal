package net.lugami.basic.commands;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySoundCommand {

    @Command(names={"playsound"}, permission="op")
    public static void playSound(Player player, @Param(name="sound") Sound sound, @Param(name="volume") float volume, @Param(name="pitch") float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}

