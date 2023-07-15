package com.lunarclient.bukkitapi.nethandler.client;

import com.lunarclient.bukkitapi.nethandler.server.LCPacketVoice;
import com.lunarclient.bukkitapi.nethandler.server.LCPacketVoiceChannel;
import com.lunarclient.bukkitapi.nethandler.server.LCPacketVoiceChannelRemove;
import com.lunarclient.bukkitapi.nethandler.server.LCPacketVoiceChannelUpdate;
import com.lunarclient.bukkitapi.nethandler.shared.LCNetHandler;

public interface LCNetHandlerClient extends LCNetHandler {

    void handleBossBar(LCPacketBossBar packet);
    void handleCooldown(LCPacketCooldown packet);
    void handleGhost(LCPacketGhost packet);
    void handleAddHologram(LCPacketHologram packet);
    void handleRemoveHologram(LCPacketHologramRemove packet);
    void handleUpdateHologram(LCPacketHologramUpdate packet);
    void handleOverrideNametags(LCPacketNametagsOverride packet);
    void handleNametagsUpdate(LCPacketNametagsUpdate packet);
    void handleNotification(LCPacketNotification packet);
    void handleServerRule(LCPacketServerRule packet);
    void handleServerUpdate(LCPacketServerUpdate packet);
    void handleStaffModState(LCPacketStaffModState packet);
    void handleTeammates(LCPacketTeammates packet);
    void handleTitle(LCPacketTitle packet);
    void handleUpdateWorld(LCPacketUpdateWorld packet);
    void handleWorldBorder(LCPacketWorldBorder packet);
    void handleWorldBorderCreateNew(LCPacketWorldBorderCreateNew packet);
    void handleWorldBorderRemove(LCPacketWorldBorderRemove packet);
    void handleWorldBorderUpdate(LCPacketWorldBorderUpdate packet);
    void handleWorldBorderUpdateNew(LCPacketWorldBorderUpdateNew packet);
    void handleVoice(LCPacketVoice packet);
    void handleVoiceChannels(LCPacketVoiceChannel packet);
    void handleVoiceChannelUpdate(LCPacketVoiceChannelUpdate packet);
    void handleVoiceChannelDelete(LCPacketVoiceChannelRemove packet);

}