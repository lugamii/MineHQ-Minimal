package org.bukkit.command;

/**
 * Represents a class which contains a single method for executing commands
 */
public interface CommandExecutor {

    /**
     * Executes the given commands, returning its success
     *
     * @param sender Source of the commands
     * @param command Command which was executed
     * @param label Alias of the commands which was used
     * @param args Passed commands arguments
     * @return true if a valid commands, otherwise false
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
