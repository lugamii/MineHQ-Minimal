package net.lugami.bridge.bukkit.auth.prompt;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public class InitialDisclaimerPrompt extends StringPrompt {

    public String getPromptText(ConversationContext context) {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Take a minute to read over this, it's important. "
                + ChatColor.RED + "2FA can be enabled to protect against hackers getting into your Minecraft account. " +
                "If you enable 2FA, you'll be required to enter a code every time you log in. If you lose your 2FA device, you wont be able to log into the Bridge Network."
                + ChatColor.GRAY + " If you have read the above and would like to proceed, type \"yes\" in the chat. Otherwise, type anything else.";
    }

    public Prompt acceptInput(ConversationContext context, String s) {
        if(s.equalsIgnoreCase("yes")) {
            return new ScanMapPrompt();
        } else {
            context.getForWhom().sendRawMessage(ChatColor.GREEN + "Aborted 2FA setup.");
            return Prompt.END_OF_CONVERSATION;
        }
    }

}
