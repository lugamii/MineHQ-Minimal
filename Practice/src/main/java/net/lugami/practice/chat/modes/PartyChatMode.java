package net.lugami.practice.chat.modes;

import net.lugami.qlib.chat.ChatHandler;
import net.lugami.qlib.chat.ChatPlayer;
import net.lugami.qlib.chat.ChatPopulator;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.lugami.practice.Practice;
import net.lugami.practice.party.Party;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PartyChatMode extends ChatPopulator {

    private static final Map<UUID, Long> canUsePartyChat = new ConcurrentHashMap<>();

    @Command(names = {"partychat"}, permission = "", description = "Messages in Party chat mode")
    public static void partychat(Player player, @Param(name = "message", wildcard = true, defaultValue = "   ") String message) {
        Party party = Practice.getInstance().getPartyHandler().getParty(player);
        if (party == null) {
            player.sendMessage(ChatColor.RED + "You aren't in a party!");
            return;
        }

        if (canUsePartyChat.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "Wait a bit before sending another message.");
            return;
        }

        ChatPlayer chatPlayer = ChatHandler.getChatPlayer(player.getUniqueId());
        ChatPopulator chatPopulator = new PartyChatMode();
        if (message.equals("   ")) {
            chatPlayer.setActiveType(chatPopulator);
        } else {
            if (!chatPlayer.hasAccess(chatPopulator, true)) {
                player.sendMessage(ChatColor.RED + "You must be in a party to chat in party chat.");
                return;
            }
            canUsePartyChat.put(player.getUniqueId(), System.currentTimeMillis() + 2_000);
            chatPopulator.handleChat(player, message);
        }
    }

    public PartyChatMode() {
        super(Practice.getInstance(), "Party", 4);
    }

    @Override
    public String getCommandParam() {
        return "/partychat [message]";
    }

    @Override
    public char getChatChar() {
        return '@';
    }

    @Override
    public String layout(Player player, String message) {
        Party party = Practice.getInstance().getPartyHandler().getParty(player);
        ChatColor prefixColor = party.isLeader(player.getUniqueId()) ? ChatColor.AQUA : ChatColor.LIGHT_PURPLE;
        return prefixColor.toString() + ChatColor.BOLD + "[P] " + player.getName() + ": " + ChatColor.LIGHT_PURPLE + message;
    }

    @Override
    public void handleChat(Player player, String message) {
        Party party = Practice.getInstance().getPartyHandler().getParty(player);
        String msg = checkMessage(player, message);
        if(msg == null) return;
        if (party == null) return;
        for (Party parties : Practice.getInstance().getPartyHandler().getParties()) {
            parties.message(layout(player, msg));
            Practice.getInstance().getLogger().info("[Party Chat] " + player.getName() + ": " + message);
        }
    }
}