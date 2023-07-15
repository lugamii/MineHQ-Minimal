package net.lugami.bridge.bukkit.commands.auth;

import net.lugami.bridge.bukkit.auth.prompt.InitialDisclaimerPrompt;
import net.lugami.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;

public class Setup2faCommand {

    @Command(names = {"setup2fa", "2fasetup"}, permission = "basic.staff", description = "Sign up to use 2FA to verify your identity", hidden = true)
    public static void setup2fa(Player sender) {
        if (!BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(sender.getUniqueId()).getSecretKey().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You already have 2FA setup!");
        } else {
            ConversationFactory factory = (new ConversationFactory(Bridge.getInstance())).withFirstPrompt(new InitialDisclaimerPrompt()).withLocalEcho(false).thatExcludesNonPlayersWithMessage("Go away evil console!");
            Conversation con = factory.buildConversation(sender);
            sender.beginConversation(con);
        }
    }
}
