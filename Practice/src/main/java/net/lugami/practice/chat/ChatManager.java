package net.lugami.practice.chat;

import net.lugami.practice.chat.listener.ChatListener;
import net.lugami.practice.party.Party;
import net.lugami.qlib.chat.ChatHandler;
import net.lugami.qlib.chat.ChatPlayer;
import net.lugami.qlib.chat.ChatPopulator;
import net.lugami.qlib.util.TaskUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import net.lugami.practice.Practice;

import java.util.ArrayList;
import java.util.List;

public class ChatManager implements Listener {

    public static List<ChatPopulator> modes;

    public ChatManager() {
        modes = new ArrayList<>();
        Practice.getInstance().getServer().getPluginManager().registerEvents(new ChatListener(), Practice.getInstance());
    }

    public static ChatPopulator findByName(String name) {
        for (ChatPopulator populator : modes) {
            if (populator.getName() == name) {
                return populator;
            }
        }
        return null;
    }

    public static void refresh(Player player) {
        refresh(player, false);
        TaskUtil.runLater(() -> refresh(player, false), 42L);

    }

    public static void refresh(Player player, boolean s) {
        ChatPlayer chatPlayer = ChatHandler.getChatPlayer(player.getUniqueId());
        Party team = Practice.getInstance().getPartyHandler().getParty(player.getUniqueId());
        chatPlayer.registerProvider(new ChatPopulator.PublicChatProvider());
//        if (team != null) {
//            if (team.getMembers().contains(player.getUniqueId())) {
//                chatPlayer.registerProvider(new PartyChatMode());
//            }
//        } else {
//            chatPlayer.setSelectedPopulator(new ChatPopulator.PublicChatProvider());
//        }
        ChatPopulator defaultPubFormat = new ChatPopulator.PublicChatProvider();
        if (chatPlayer.getRegisteredPopulators().contains(defaultPubFormat)) {
            chatPlayer.removeProvider(defaultPubFormat);
        }
    }

    public static void update(Player player, ChatPopulator populator) {
        Practice.getInstance().getChatModeMap().setPopulator(player.getUniqueId(), populator);
    }

}