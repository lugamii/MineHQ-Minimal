package com.cheatbreaker.nethandler.server;

import com.cheatbreaker.nethandler.ICBNetHandler;
import com.cheatbreaker.nethandler.client.CBPacketClientVoice;
import com.cheatbreaker.nethandler.client.CBPacketVoiceChannelSwitch;
import com.cheatbreaker.nethandler.client.CBPacketVoiceMute;

public interface ICBNetHandlerServer extends ICBNetHandler {

    void handleVoice(CBPacketClientVoice var1);

    void handleVoiceChannelSwitch(CBPacketVoiceChannelSwitch var1);

    void handleVoiceMute(CBPacketVoiceMute var1);

}
