package net.lugami.practice.follow.command;

import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.practice.Practice;
import net.lugami.practice.match.commands.LeaveCommand;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class SilentFollowCommand {

    @Command(names = {"silentfollow", "sf"}, permission = "basic.staff")
    public static void silentfollow(Player sender, @Param(name = "target") Player target) {
        sender.setMetadata("ModMode", new FixedMetadataValue(Practice.getInstance(), true));
        sender.setMetadata("invisible", new FixedMetadataValue(Practice.getInstance(), true));

        if (Practice.getInstance().getPartyHandler().hasParty(sender)) {
            LeaveCommand.leave(sender);
        }

        FollowCommand.follow(sender, target);
    }

}
