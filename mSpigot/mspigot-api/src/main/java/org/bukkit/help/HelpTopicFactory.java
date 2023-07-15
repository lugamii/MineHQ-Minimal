package org.bukkit.help;

import org.bukkit.command.Command;

/**
 * A HelpTopicFactory is used to create custom {@link HelpTopic} objects from
 * commands that inherit from a common base class or have executors that
 * inherit from a common base class. You can use a custom HelpTopic to change
 * the way all the commands in your plugin display in the help. If your plugin
 * implements a complex permissions system, a custom help topic may also be
 * appropriate.
 * <p>
 * To automatically bind your plugin's commands to your custom HelpTopic
 * implementation, first make sure all your commands or executors derive from
 * a custom base class (it doesn't have to do anything). Next implement a
 * custom HelpTopicFactory that accepts your custom commands base class and
 * instantiates an instance of your custom HelpTopic from it. Finally,
 * register your HelpTopicFactory against your commands base class using the
 * {@link HelpMap#registerHelpTopicFactory(Class, HelpTopicFactory)} method.
 * <p>
 * As the help system iterates over all registered commands to make help
 * topics, it first checks to see if there is a HelpTopicFactory registered
 * for the commands's base class. If so, the factory is used to make a help
 * topic rather than a generic help topic. If no factory is found for the
 * commands's base class and the commands derives from {@link
 * org.bukkit.command.PluginCommand}, then the type of the commands's executor
 * is inspected looking for a registered HelpTopicFactory. Finally, if no
 * factory is found, a generic help topic is created for the commands.
 *
 * @param <TCommand> The base class for your custom commands.
 */
public interface HelpTopicFactory<TCommand extends Command> {
    /**
     * This method accepts a commands deriving from a custom commands base class
     * and constructs a custom HelpTopic for it.
     *
     * @param command The custom commands to build a help topic for.
     * @return A new custom help topic or {@code null} to intentionally NOT
     *     create a topic.
     */
    public HelpTopic createTopic(TCommand command);
}
