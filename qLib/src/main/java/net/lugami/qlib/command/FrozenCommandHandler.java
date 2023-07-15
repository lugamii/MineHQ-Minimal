package net.lugami.qlib.command;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.lugami.qlib.command.bukkit.FrozenCommand;
import net.lugami.qlib.command.bukkit.FrozenCommandMap;
import net.lugami.qlib.command.bukkit.FrozenHelpTopic;
import net.lugami.qlib.command.defaults.BuildCommand;
import net.lugami.qlib.command.defaults.CommandInfoCommand;
import net.lugami.qlib.command.defaults.EvalCommand;
import net.lugami.qlib.command.defaults.VisibilityDebugCommand;
import net.lugami.qlib.command.parameter.*;
import net.lugami.qlib.command.parameter.filter.NormalFilter;
import net.lugami.qlib.command.parameter.filter.StrictFilter;
import net.lugami.qlib.command.parameter.offlineplayer.OfflinePlayerWrapper;
import net.lugami.qlib.command.parameter.offlineplayer.OfflinePlayerWrapperParameterType;
import net.lugami.qlib.command.utils.EasyClass;
import net.lugami.qlib.command.parameter.*;
import net.lugami.qlib.qLib;
import net.lugami.qlib.util.ClassUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class FrozenCommandHandler {

    public static CommandNode ROOT_NODE = new CommandNode();
    protected static Map<Class<?>, ParameterType<?>> PARAMETER_TYPE_MAP = new HashMap<>();
    protected static CommandMap commandMap;
    protected static Map<String, Command> knownCommands;
    private static CommandConfiguration config;

    public static void init() {
        FrozenCommandHandler.registerClass(BuildCommand.class);
        FrozenCommandHandler.registerClass(EvalCommand.class);
        FrozenCommandHandler.registerClass(CommandInfoCommand.class);
        FrozenCommandHandler.registerClass(VisibilityDebugCommand.class);
        new BukkitRunnable(){

            public void run() {
                try {
                    FrozenCommandHandler.swapCommandMap();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(qLib.getInstance(), 5L);
    }

    public static void registerParameterType(Class<?> clazz, ParameterType<?> type) {
        PARAMETER_TYPE_MAP.put(clazz, type);
    }

    public static ParameterType<?> getParameterType(Class<?> clazz) {
        return PARAMETER_TYPE_MAP.get(clazz);
    }

    public static CommandConfiguration getConfig() {
        return config;
    }

    public static void setConfig(CommandConfiguration config) {
        FrozenCommandHandler.config = config;
    }

    public static void registerMethod(Method method) {
        method.setAccessible(true);
        Set<CommandNode> nodes = new MethodProcessor().process(method);
        if (nodes != null) {
            nodes.forEach(node -> {
                if (node != null) {
                    FrozenCommand command = new FrozenCommand(node, JavaPlugin.getProvidingPlugin(method.getDeclaringClass()));
                    FrozenCommandHandler.register(command);
                    node.getChildren().values().forEach(n -> FrozenCommandHandler.registerHelpTopic(n, node.getAliases()));
                }
            });
        }
    }

    protected static void registerHelpTopic(CommandNode node, Set<String> aliases) {
        if (node.method != null) {
            Bukkit.getHelpMap().addTopic(new FrozenHelpTopic(node, aliases));
        }
        if (node.hasCommands()) {
            node.getChildren().values().forEach(n -> FrozenCommandHandler.registerHelpTopic(n, null));
        }
    }

    private static void register(FrozenCommand command) {
        try {
            Map<String, Command> knownCommands = FrozenCommandHandler.getKnownCommands();
            Iterator<Map.Entry<String, Command>> iterator = knownCommands.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Command> entry = iterator.next();
                if (!entry.getValue().getName().equalsIgnoreCase(command.getName())) continue;
                entry.getValue().unregister(commandMap);
                iterator.remove();
            }
            for (String alias : command.getAliases()) {
                knownCommands.put(alias, command);
            }
            command.register(commandMap);
            knownCommands.put(command.getName(), command);
        }
        catch (Exception knownCommands) {
            // empty catch block
        }
    }

    public static void registerClass(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            FrozenCommandHandler.registerMethod(method);
        }
    }

    public static void unregisterClass(Class<?> clazz) {
        Map<String, Command> knownCommands = FrozenCommandHandler.getKnownCommands();
        Iterator<Command> iterator = knownCommands.values().iterator();
        while (iterator.hasNext()) {
            CommandNode node;
            Command command = iterator.next();
            if (!(command instanceof FrozenCommand) || ((FrozenCommand)command).getNode().getOwningClass() != clazz) continue;
            command.unregister(commandMap);
            iterator.remove();
        }
    }

    public static void registerPackage(Plugin plugin, String packageName) {
        ClassUtils.getClassesInPackage(plugin, packageName).forEach(FrozenCommandHandler::registerClass);
    }

    public static void registerAll(Plugin plugin) {
        FrozenCommandHandler.registerPackage(plugin, plugin.getClass().getPackage().getName());
    }

    private static void swapCommandMap() throws Exception {
        Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMapField.setAccessible(true);
        Object oldCommandMap = commandMapField.get(Bukkit.getServer());
        FrozenCommandMap newCommandMap = new FrozenCommandMap(Bukkit.getServer());
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(knownCommandsField, knownCommandsField.getModifiers() & -17);
        knownCommandsField.set(newCommandMap, knownCommandsField.get(oldCommandMap));
        commandMapField.set(Bukkit.getServer(), newCommandMap);
    }

    protected static CommandMap getCommandMap() {
        return (CommandMap) new EasyClass<>(Bukkit.getServer()).getField("commandMap").get();
    }

    protected static Map<String, Command> getKnownCommands() {
        return (Map<String,Command>) new EasyClass<>(commandMap).getField("knownCommands").get();
    }

    static {
        config = new CommandConfiguration().setNoPermissionMessage("&cNo permission.");
        FrozenCommandHandler.registerParameterType(Boolean.TYPE, new BooleanParameterType());
        FrozenCommandHandler.registerParameterType(Integer.TYPE, new IntegerParameterType());
        FrozenCommandHandler.registerParameterType(Double.TYPE, new DoubleParameterType());
        FrozenCommandHandler.registerParameterType(Float.TYPE, new FloatParameterType());
        FrozenCommandHandler.registerParameterType(Long.class, new LongParameterType());
        FrozenCommandHandler.registerParameterType(String.class, new StringParameterType());
        FrozenCommandHandler.registerParameterType(Player.class, new PlayerParameterType());
        FrozenCommandHandler.registerParameterType(World.class, new WorldParameterType());
        FrozenCommandHandler.registerParameterType(ItemStack.class, new ItemStackParameterType());
        FrozenCommandHandler.registerParameterType(OfflinePlayer.class, new OfflinePlayerParameterType());
        FrozenCommandHandler.registerParameterType(UUID.class, new UUIDParameterType());
        FrozenCommandHandler.registerParameterType(OfflinePlayerWrapper.class, new OfflinePlayerWrapperParameterType());
        FrozenCommandHandler.registerParameterType(NormalFilter.class, new NormalFilter());
        FrozenCommandHandler.registerParameterType(StrictFilter.class, new StrictFilter());
        commandMap = FrozenCommandHandler.getCommandMap();
        knownCommands = FrozenCommandHandler.getKnownCommands();
    }

}

