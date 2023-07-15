package org.bukkit.command;

import org.bukkit.block.Block;

public interface BlockCommandSender extends CommandSender {

    /**
     * Returns the block this commands sender belongs to
     *
     * @return Block for the commands sender
     */
    public Block getBlock();
}
