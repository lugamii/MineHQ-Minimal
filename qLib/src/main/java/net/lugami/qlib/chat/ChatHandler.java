package net.lugami.qlib.chat;

import com.google.common.base.Preconditions;
import lombok.Setter;
import net.lugami.qlib.util.CancellationDetector;
import lombok.Getter;
import net.lugami.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatHandler implements Listener {

    @Getter private static final List<ChatPlayer> chatPlayers = new ArrayList<>();
    @Getter private static boolean initiated = false;
    @Setter private static ChatPopulator defaultPopulator = new ChatPopulator.PublicChatProvider();
    @Getter private static final CancellationDetector<AsyncPlayerChatEvent> detector = new CancellationDetector<>(AsyncPlayerChatEvent.class);

    public static void init() {
        Preconditions.checkState(!ChatHandler.initiated);
        initiated = true;
        Bukkit.getPluginManager().registerEvents(new ChatHandler(), qLib.getInstance());
        ChatHandler.getDetector().addListener((plugin, event) -> {
            if(plugin != qLib.getInstance()) ChatHandler.getChatPlayer(event.getPlayer().getUniqueId()).setChatCancelled(true);
        });
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ChatPlayer chatPlayer = new ChatPlayer(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(qLib.getInstance(), () -> {
            if(chatPlayer.getSelectedPopulator() == null) {
                chatPlayer.registerProvider(defaultPopulator);
                chatPlayer.setSelectedPopulator(defaultPopulator);
            }
        }, 20);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ChatPlayer chatPlayer = getChatPlayer(player.getUniqueId());
        if(chatPlayer != null) chatPlayers.remove(chatPlayer);
    }

    @EventHandler(priority = EventPriority.HIGHEST) // Event should fire last
    public void onChat(AsyncPlayerChatEvent event) {
        if(event.isCancelled()) return; // Check if a plugin is cancelling the event
        if(!event.isAsynchronous()) return;

        Player player = event.getPlayer();
        ChatPlayer chatPlayer = getChatPlayer(player.getUniqueId());

        ChatPopulator chatPopulator = chatPlayer.getRegisteredPopulators().stream().filter(chatPopulator1 -> chatPopulator1.getChatChar() == event.getMessage().charAt(0)).findFirst().orElse(null);
        if(chatPopulator != null) {
            chatPopulator.handleChat(player, event.getMessage().substring(1));
            event.setCancelled(true);
        }
        else if(chatPlayer.getSelectedPopulator() != null)  {
            chatPlayer.getSelectedPopulator().handleChat(player, event.getMessage());
            event.setCancelled(true);
        }
    }

    public static ChatPlayer getChatPlayer(UUID uuid) {
        return chatPlayers.stream().filter(chatPlayer -> chatPlayer.getUuid().equals(uuid)).findFirst().orElse(null);
    }


}
