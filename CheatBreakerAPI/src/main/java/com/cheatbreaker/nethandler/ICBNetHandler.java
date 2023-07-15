package com.cheatbreaker.nethandler;

import com.cheatbreaker.nethandler.shared.CBPacketAddWaypoint;
import com.cheatbreaker.nethandler.shared.CBPacketRemoveWaypoint;

public interface ICBNetHandler {

    void handleAddWaypoint(CBPacketAddWaypoint var1);

    void handleRemoveWaypoint(CBPacketRemoveWaypoint var1);

}
