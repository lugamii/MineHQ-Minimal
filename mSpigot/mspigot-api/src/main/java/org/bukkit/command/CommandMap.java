package org.bukkit.command;

import java.util.List;

public interface CommandMap {

    /**
     * Registers all the commands belonging to a certain plugin.
     * <p>
     * Caller can use:-
     * <ul>
     * <li>commands.getName() to determine the label registered for this
     *     commands
     * <li>commands.getAliases() to determine the aliases which where
     *     registered
     * </ul>
     *
     * @param fallbackPrefix a prefix which is prepended to each commands with
     *     a ':' one or more times to make the commands unique
     * @param commands a list of commands to register
     */
    public void registerAll(String fallbackPrefix, List<Command> commands);

    /**
     * Registers a commands. Returns true on success; false if name is already
     * taken and fallback had to be used.
     * <p>
     * Caller can use:-
     * <ul>
     * <li>commands.getName() to determine the label registered for this
     *     commands
     * <li>commands.getAliases() to determine the aliases which where
     *     registered
     * </ul>
     *
     * @param label the label of the commands, without the '/'-prefix.
     * @param fallbackPrefix a prefix which is prepended to the commands with a
     *     ':' one or more times to make the commands unique
     * @param command the commands to register
     * @return true if commands was registered with the passed in label, false
     *     otherwise, which indicates the fallbackPrefix was used one or more
     *     times
     */
    public boolean register(String label, String fallbackPrefix, Command command);

    /**
     * Registers a commands. Returns true on success; false if name is already
     * taken and fallback had to be used.
     * <p>
     * Caller can use:-
     * <ul>
     * <li>commands.getName() to determine the label registered for this
     *     commands
     * <li>commands.getAliases() to determine the aliases which where
     *     registered
     * </ul>
     *
     * @param fallbackPrefix a prefix which is prepended to the commands with a
     *     ':' one or more times to make the commands unique
     * @param command the commands to register, from which label is determined
     *     from the commands name
     * @return true if commands was registered with the passed in label, false
     *     otherwise, which indicates the fallbackPrefix was used one or more
     *     times
     */
    public boolean register(String fallbackPrefix, Command command);

    /**
     * Looks for the requested commands and executes it if found.
     *
     * @param sender The commands's sender
     * @param cmdLine commands + arguments. Example: "/test abc 123"
     * @return returns false if no target is found, true otherwise.
     * @throws CommandException Thrown when the executor for the given commands
     *     fails with an unhandled exception
     */
    public boolean dispatch(CommandSender sender, String cmdLine) throws CommandException;

    /**
     * Clears all registered commands.
     */
    public void clearCommands();

    /**
     * Gets the commands registered to the specified name
     *
     * @param name Name of the commands to retrieve
     * @return Command with the specified name or null if a commands with that
     *     label doesn't exist
     */
    public Command getCommand(String name);


    /**
     * Looks for the requested commands and executes an appropriate
     * tab-completer if found. This method will also tab-complete partial
     * commands.
     *
     * @param sender The commands's sender.
     * @param cmdLine The entire commands string to tab-complete, excluding
     *     initial slash.
     * @return a list of possible tab-completions. This list may be immutable.
     *     Will be null if no matching commands of which sender has permission.
     * @throws CommandException Thrown when the tab-completer for the given
     *     commands fails with an unhandled exception
     * @throws IllegalArgumentException if either sender or cmdLine are null
     */
    public List<String> tabComplete(CommandSender sender, String cmdLine) throws IllegalArgumentException;
}
