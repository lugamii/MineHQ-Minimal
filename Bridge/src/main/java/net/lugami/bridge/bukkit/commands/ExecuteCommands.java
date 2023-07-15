package net.lugami.bridge.bukkit.commands;

import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.packet.types.NetworkBroadcastPacket;
import net.lugami.bridge.global.packet.types.ServerCommandPacket;
import net.lugami.bridge.global.packet.types.ServerGroupCommandPacket;
import net.lugami.qlib.command.Command;
import net.lugami.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import net.lugami.bridge.bukkit.Bridge;

public class ExecuteCommands {

    @Command(names = {"execcmd server", "executecommands server"}, permission = "bridge.execute", hidden = true, async = true)
    public static void server(Player sender, @Param(name = "server") String server, @Param(name = "command", wildcard = true) String command) {
        promptExecute(sender, server, command);
    }

    @Command(names = {"execcmd global", "executecommands global"}, permission = "bridge.execute", hidden = true, async = true)
    public static void global(Player sender, @Param(name = "command", wildcard = true) String command) {
        promptExecute(sender, "globally", command);
    }

    @Command(names = {"execcmd group", "executecommands group"}, permission = "bridge.execute", hidden = true, async = true)
    public static void group(Player sender, @Param(name = "group") String serverGroup, @Param(name = "command", wildcard = true) String command) {
        promptExecute2(sender, serverGroup, command);
    }

    private static void promptExecute(Player player, String server, String command) {
        ConversationFactory factory = (new ConversationFactory(Bridge.getInstance())).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(ConversationContext context) {
                return ChatColor.translateAlternateColorCodes('&', "&eYou are about to execute the command &c\"" + command + "\" &c" + server + " &eare you sure you want to do this? If so type &ayes&c.");
            }

            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("yes")) {
                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Sending cross-bungee command...");
                    PacketHandler.sendToAll(new ServerCommandPacket(server, command));
                    PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.execute.view", "&8[&eServer Monitor&8] " + player.getDisplayName() + " &fexecuted command &d" + command + (server.equals("globally") ? " &f" : " &fon ") + server));
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.RED + "Executing canclled.");
                }
                return Prompt.END_OF_CONVERSATION;
            }
        }).withEscapeSequence("/no").withLocalEcho(false).withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(player);
        player.beginConversation(con);
    }

    private static void promptExecute2(Player player, String serverGroup, String command) {
        ConversationFactory factory = (new ConversationFactory(Bridge.getInstance())).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(ConversationContext context) {
                return ChatColor.translateAlternateColorCodes('&', "&eYou are about to execute the command &c\"" + command + "\" to the server group &c" + serverGroup + " &eare you sure you want to do this? If so type &ayes&c.");
            }

            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("yes")) {
                    context.getForWhom().sendRawMessage(ChatColor.GREEN + "Sending cross-bungee command...");
                    PacketHandler.sendToAll(new ServerGroupCommandPacket(serverGroup, command));
                    PacketHandler.sendToAll(new NetworkBroadcastPacket("bridge.execute.view", "&8[&eServer Monitor&8] " + player.getDisplayName() + " &fexecuted command &d" + command + " &fon the server group " + serverGroup));
                } else {
                    context.getForWhom().sendRawMessage(ChatColor.RED + "Executing canclled.");
                }
                return Prompt.END_OF_CONVERSATION;
            }
        }).withEscapeSequence("/no").withLocalEcho(false).withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(player);
        player.beginConversation(con);
    }
}