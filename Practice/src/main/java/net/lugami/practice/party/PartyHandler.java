package net.lugami.practice.party;

import com.google.common.collect.ImmutableSet;

import net.lugami.practice.chat.modes.PartyChatMode;
import net.lugami.practice.party.listener.PartyItemListener;
import net.lugami.practice.party.listener.PartyLeaveListener;
import net.lugami.qlib.chat.ChatHandler;
import net.lugami.qlib.chat.ChatPlayer;
import net.lugami.practice.Practice;
import net.lugami.practice.chat.ChatManager;
import net.lugami.practice.util.InventoryUtils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PartyHandler {

    static final int INVITE_EXPIRATION_SECONDS = 30;

    private final Set<Party> parties = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<UUID, Party> playerPartyCache = new ConcurrentHashMap<>();

    public PartyHandler() {
        Bukkit.getPluginManager().registerEvents(new ChatManager(), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new PartyItemListener(this), Practice.getInstance());
        Bukkit.getPluginManager().registerEvents(new PartyLeaveListener(), Practice.getInstance());
    }

    public Set<Party> getParties() {
        return ImmutableSet.copyOf(parties);
    }

    public boolean hasParty(Player player) {
            ChatPlayer chatPlayer = ChatHandler.getChatPlayer(player.getUniqueId());
           if (getParty(player.getUniqueId()) != null) {
               chatPlayer.registerProvider(new PartyChatMode());
               return playerPartyCache.containsKey(player.getUniqueId());
           }else {
               chatPlayer.removeProvider(new PartyChatMode());
            return playerPartyCache.containsKey(player.getUniqueId());
        }
    }

    public Party getParty(Player player) {
        ChatPlayer chatPlayer = ChatHandler.getChatPlayer(player.getUniqueId());
        if (getParty(player.getUniqueId()) != null) {
            chatPlayer.registerProvider(new PartyChatMode());
            return playerPartyCache.get(player.getUniqueId());
        }else {
            chatPlayer.removeProvider(new PartyChatMode());
            return playerPartyCache.get(player.getUniqueId());
        }
    }

    public Party getParty(UUID uuid) {
        return playerPartyCache.get(uuid);
    }

    public Party getOrCreateParty(Player player) {
        Party party = getParty(player);

        if (party == null) {
            party = new Party(player.getUniqueId());
            parties.add(party);
            InventoryUtils.resetInventoryDelayed(player);
        }

        return party;
    }

    void unregisterParty(Party party) {
        parties.remove(party);
    }

    public void updatePartyCache(UUID playerUuid, Party party) {
        if (party != null) {
            playerPartyCache.put(playerUuid, party);
        } else {
            playerPartyCache.remove(playerUuid);
        }
    }

}