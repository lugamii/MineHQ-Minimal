package net.lugami.practice.duel.command;

import net.lugami.practice.kittype.menu.select.SelectKitTypeMenu;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import net.lugami.qlib.util.UUIDUtils;
import net.lugami.practice.PracticeLang;
import net.lugami.practice.Practice;
import net.lugami.practice.duel.DuelHandler;
import net.lugami.practice.duel.DuelInvite;
import net.lugami.practice.duel.PartyDuelInvite;
import net.lugami.practice.duel.PlayerDuelInvite;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.lobby.LobbyHandler;
import net.lugami.practice.party.Party;
import net.lugami.practice.party.PartyHandler;
import net.lugami.practice.validation.PracticeValidation;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class DuelCommand {

    @Command(names = {"duel", "1v1"}, permission = "")
    public static void duel(Player sender, @Param(name = "player") Player target) {
        if (sender == target) {
            sender.sendMessage(ChatColor.RED + "You can't duel yourself!");
            return;
        }

        PartyHandler partyHandler = Practice.getInstance().getPartyHandler();
        LobbyHandler LobbyHandler = Practice.getInstance().getLobbyHandler();

        Party senderParty = partyHandler.getParty(sender);
        Party targetParty = partyHandler.getParty(target);

        if (senderParty != null && targetParty != null) {
            // party dueling party (legal)
            if (!PracticeValidation.canSendDuel(senderParty, targetParty, sender)) {
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();

                // reassign these fields so that any party changes
                // (kicks, etc) are reflectednow
                Party newSenderParty = partyHandler.getParty(sender);
                Party newTargetParty = partyHandler.getParty(target);

                if (newSenderParty != null && newTargetParty != null) {
                    if (newSenderParty.isLeader(sender.getUniqueId())) {
                        duel(sender, newSenderParty, newTargetParty, kitType);
                    } else {
                        sender.sendMessage(PracticeLang.NOT_LEADER_OF_PARTY);
                    }
                }
            }, "Select a kit type...").openMenu(sender);
        } else if (senderParty == null && targetParty == null) {
            // player dueling player (legal)
            if (!PracticeValidation.canSendDuel(sender, target)) {
                return;
            }

            if (target.hasPermission("practice.famous") && System.currentTimeMillis() - LobbyHandler.getLastLobbyTime(target) < 3_000) {
                sender.sendMessage(ChatColor.RED + target.getName() + " just returned to the player, please wait a moment.");
                return;
            }

            new SelectKitTypeMenu(kitType -> {
                sender.closeInventory();
                duel(sender, target, kitType);
            }, "Select a kit type...").openMenu(sender);
        } else if (senderParty == null) {
            // player dueling party (illegal)
            sender.sendMessage(ChatColor.RED + "You must create a party to duel " + target.getName() + "'s party.");
        } else {
            // party dueling player (illegal)
            sender.sendMessage(ChatColor.RED + "You must leave your party to duel " + target.getName() + ".");
        }
    }

    public static void duel(Player sender, Player target, KitType kitType) {
        if (!PracticeValidation.canSendDuel(sender, target)) {
            return;
        }

        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(target, sender);

        // if two players duel each other for the same thing automatically
        // accept it to make their life a bit easier.
        if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType) {
            AcceptCommand.accept(sender, target);
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(sender, target);

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getKitType() == kitType) {
                sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + " to a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
                return;
            } else {
                // if an invite was already sent (with a different kit type)
                // just delete it (so /accept will accept the 'latest' invite)
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        target.sendMessage(ChatColor.AQUA + sender.getName() + ChatColor.YELLOW + " has sent you a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
        target.spigot().sendMessage(createInviteNotification(sender.getName()));

        sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel invite to " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + ".");
        duelHandler.insertInvite(new PlayerDuelInvite(sender, target, kitType));
    }

    public static void duel(Player sender, Party senderParty, Party targetParty, KitType kitType) {
        if (!PracticeValidation.canSendDuel(senderParty, targetParty, sender)) {
            return;
        }

        DuelHandler duelHandler = Practice.getInstance().getDuelHandler();
        DuelInvite autoAcceptInvite = duelHandler.findInvite(targetParty, senderParty);
        String targetPartyLeader = UUIDUtils.name(targetParty.getLeader());

        // if two players duel each other for the same thing automatically
        // accept it to make their life a bit easier.
        if (autoAcceptInvite != null && autoAcceptInvite.getKitType() == kitType) {
            AcceptCommand.accept(sender, Bukkit.getPlayer(targetParty.getLeader()));
            return;
        }

        DuelInvite alreadySentInvite = duelHandler.findInvite(senderParty, targetParty);

        if (alreadySentInvite != null) {
            if (alreadySentInvite.getKitType() == kitType) {
                sender.sendMessage(ChatColor.YELLOW + "You have already invited " + ChatColor.AQUA + targetPartyLeader + "'s party" + ChatColor.YELLOW + " to a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
                return;
            } else {
                // if an invite was already sent (with a different kit type)
                // just delete it (so /accept will accept the 'latest' invite)
                duelHandler.removeInvite(alreadySentInvite);
            }
        }

        targetParty.message(ChatColor.AQUA + sender.getName() + "'s Party (" + senderParty.getMembers().size() + ")" + ChatColor.YELLOW + " has sent you a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel.");
        Bukkit.getPlayer(targetParty.getLeader()).spigot().sendMessage(createInviteNotification(sender.getName()));

        sender.sendMessage(ChatColor.YELLOW + "Successfully sent a " + kitType.getColoredDisplayName() + ChatColor.YELLOW + " duel invite to " + ChatColor.AQUA + targetPartyLeader + "'s party" + ChatColor.YELLOW + ".");
        duelHandler.insertInvite(new PartyDuelInvite(senderParty, targetParty, kitType));
    }

    private static TextComponent[] createInviteNotification(String sender) {
        TextComponent firstPart = new TextComponent("Click here or type ");
        TextComponent commandPart = new TextComponent("/accept " + sender);
        TextComponent secondPart = new TextComponent(" to accept the invite");

        firstPart.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        commandPart.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        secondPart.setColor(net.md_5.bungee.api.ChatColor.GOLD);

        ClickEvent.Action runCommand = ClickEvent.Action.RUN_COMMAND;
        HoverEvent.Action showText = HoverEvent.Action.SHOW_TEXT;

        firstPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        firstPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[] { new TextComponent(ChatColor.GREEN + "Click here to accept") }));

        commandPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        commandPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[] { new TextComponent(ChatColor.GREEN + "Click here to accept") }));

        secondPart.setClickEvent(new ClickEvent(runCommand, "/accept " + sender));
        secondPart.setHoverEvent(new HoverEvent(showText, new BaseComponent[] { new TextComponent(ChatColor.GREEN + "Click here to accept") }));

        return new TextComponent[] { firstPart, commandPart, secondPart };
    }

}