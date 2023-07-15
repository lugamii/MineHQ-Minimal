package net.lugami.practice.duel;

import java.util.UUID;

import net.lugami.practice.kittype.KitType;
import org.bukkit.entity.Player;

public final class PlayerDuelInvite extends DuelInvite<UUID> {

    public PlayerDuelInvite(Player sender, Player target, KitType kitType) {
        super(sender.getUniqueId(), target.getUniqueId(), kitType);
    }

}