package com.cheatbreaker.nethandler.client;

import com.cheatbreaker.nethandler.ICBNetHandler;
import com.cheatbreaker.nethandler.server.*;

public interface ICBNetHandlerClient extends ICBNetHandler {

    void handleCooldown(CBPacketCooldown var1);

    void handleNotification(CBPacketNotification var1);

    void handleStaffModState(CBPacketStaffModState var1);

    void handleNametagsUpdate(CBPacketUpdateNametags var1);

    void handleTeammates(CBPacketTeammates var1);

    void handleOverrideNametags(CBPacketOverrideNametags var1);

    void handleAddHologram(CBPacketAddHologram var1);

    void handleUpdateHologram(CBPacketUpdateHologram var1);

    void handleRemoveHologram(CBPacketRemoveHologram var1);

    void handleTitle(CBPacketTitle var1);

    void handleServerRule(CBPacketServerRule var1);

    void handleVoice(CBPacketVoice var1);

    void handleVoiceChannels(CBPacketVoiceChannel var1);

    void handleVoiceChannelUpdate(CBPacketVoiceChannelUpdate var1);

    void handleDeleteVoiceChannel(CBPacketDeleteVoiceChannel var1);

    void handleUpdateWorld(CBPacketUpdateWorld var1);

    void handleServerUpdate(CBPacketServerUpdate var1);

    void handleWorldBorder(CBPacketWorldBorder var1);

    void handleWorldBorderUpdate(CBPacketWorldBorderUpdate var1);

    void handleWorldBorderRemove(CBPacketWorldBorderRemove var1);

}
