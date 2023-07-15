package com.lunarclient.bukkitapi.nethandler.server;

import com.lunarclient.bukkitapi.nethandler.client.LCPacketClientVoice;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketVoiceChannelSwitch;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketVoiceMute;
import com.lunarclient.bukkitapi.nethandler.shared.LCNetHandler;

public interface LCNetHandlerServer extends LCNetHandler {

    void handleStaffModStatus(LCPacketStaffModStatus packet);
    void handleVoice(LCPacketClientVoice packet);
    void handleVoiceMute(LCPacketVoiceMute packet);
    void handleVoiceChannelSwitch(LCPacketVoiceChannelSwitch packet);
}