package net.lugami.qlib.command.bukkit;

import net.lugami.qlib.command.CommandNode;
import org.bukkit.*;
import org.apache.commons.lang.*;
import org.bukkit.command.Command;
import org.bukkit.entity.*;
import org.bukkit.util.*;
import org.bukkit.command.*;
import java.util.*;

public class FrozenCommandMap extends SimpleCommandMap {

    public FrozenCommandMap(final Server server) {
        super(server);
    }

    public List<String> tabComplete(final CommandSender sender, final String cmdLine) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(cmdLine, "Command line cannot null");
        final int spaceIndex = cmdLine.indexOf(32);
        if (spaceIndex == -1) {
            final ArrayList<String> completions = new ArrayList<>();
            final Map<String, Command> knownCommands = this.knownCommands;
            final String prefix = (sender instanceof Player) ? "/" : "";
            for (final Map.Entry<String, Command> commandEntry : knownCommands.entrySet()) {
                final String name = commandEntry.getKey();
                if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {
                    final Command command = commandEntry.getValue();
                    if (command instanceof FrozenCommand) {
                        CommandNode executionNode = ((FrozenCommand)command).node.getCommand(name);
                        if (executionNode == null) {
                            executionNode = ((FrozenCommand)command).node;
                        }
                        if (!executionNode.hasCommands()) {
                            CommandNode testNode = executionNode.getCommand(name);
                            if (testNode == null) {
                                testNode = ((FrozenCommand)command).node.getCommand(name);
                            }
                            if (!testNode.canUse(sender)) {
                                continue;
                            }
                            completions.add(prefix + name);
                        }
                        else {
                            if (executionNode.getSubCommands(sender, false).size() == 0) {
                                continue;
                            }
                            completions.add(prefix + name);
                        }
                    }
                    else {
                        if (!command.testPermissionSilent(sender)) {
                            continue;
                        }
                        completions.add(prefix + name);
                    }
                }
            }
            completions.sort(String.CASE_INSENSITIVE_ORDER);
            return completions;
        }
        final String commandName = cmdLine.substring(0, spaceIndex);
        final Command target = this.getCommand(commandName);
        if (target == null) {
            return null;
        }
        if (!target.testPermissionSilent(sender)) {
            return null;
        }
        final String argLine = cmdLine.substring(spaceIndex + 1);
        final String[] args = argLine.split(" ");
        try {
            final List<String> completions2 = (target instanceof FrozenCommand) ? ((FrozenCommand)target).tabComplete(sender, cmdLine) : target.tabComplete(sender, commandName, args);
            if (completions2 != null) completions2.sort(String.CASE_INSENSITIVE_ORDER);
            return completions2;
        }
        catch (CommandException ex) {
            throw ex;
        }
        catch (Throwable ex2) {
            throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex2);
        }
    }
}