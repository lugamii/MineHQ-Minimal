package net.lugami.basic;

import net.lugami.basic.chat.ChatManager;
import net.lugami.basic.chat.MessagingManager;
import net.lugami.basic.commands.GSSCommand;
import net.lugami.basic.commands.GlintCommand;
import net.lugami.basic.commands.SetSlotsCommand;
import net.lugami.basic.hologram.HologramManager;
import net.lugami.basic.server.ServerManager;
import net.lugami.basic.commands.parameter.EnchantmentParameterType;
import net.lugami.basic.commands.parameter.EntityTypeParameterType;
import net.lugami.basic.commands.parameter.GameModeParameterType;
import net.lugami.basic.commands.parameter.MonitorTarget;
import net.lugami.basic.commands.parameter.SoundParameterType;
import net.lugami.basic.listener.BasicInventoryListener;
import net.lugami.basic.listener.ChatFormatListener;
import net.lugami.basic.listener.ChatListener;
import net.lugami.basic.listener.ColoredSignListener;
import net.lugami.basic.listener.DisallowedCommandsListener;
import net.lugami.basic.listener.FrozenPlayerListener;
import net.lugami.basic.listener.FrozenServerListener;
import net.lugami.basic.listener.GlintListener;
import net.lugami.basic.listener.HeadNameListener;
import net.lugami.basic.listener.TeleportationListener;
import net.lugami.qlib.command.FrozenCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Basic extends JavaPlugin {

    private static Basic instance;
    private String networkWebsite;
    private String teamSpeakIp;
    private long startupTime;
    private ServerManager serverManager;
    private MessagingManager messagingManager;
    private ChatManager chatManager;
    private HologramManager hologramManager;
    private Listener chatListener;

    public void onEnable() {
        instance = this;
        instance.saveDefaultConfig();
        this.reloadConfig();
        this.networkWebsite = this.getConfig().getString("text.network-website");
        this.teamSpeakIp = this.getConfig().getString("text.teamspeak");
        SetSlotsCommand.load();
        this.startupTime = System.currentTimeMillis();
        try {
            this.setupManagers();
            this.registerCommands();
            this.registerListeners();
        }
        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Basic] An error has occurred when initializing the plugin. Please look at the stacktrace above.");
        }
    }

    public void onDisable() {
        instance = null;
    }

    private void setupManagers() {
        this.serverManager = new ServerManager();
        this.messagingManager = new MessagingManager();
        this.chatManager = new ChatManager();
        this.hologramManager = new HologramManager();
    }

    private void registerCommands() {
        FrozenCommandHandler.registerAll(this);
        FrozenCommandHandler.registerParameterType(EntityType.class, new EntityTypeParameterType());
        FrozenCommandHandler.registerParameterType(GameMode.class, new GameModeParameterType());
        FrozenCommandHandler.registerParameterType(Enchantment.class, new EnchantmentParameterType());
        FrozenCommandHandler.registerParameterType(Sound.class, new SoundParameterType());
        FrozenCommandHandler.registerParameterType(MonitorTarget.class, new MonitorTarget.Type());
        GlintCommand.registerAdapter();
        GSSCommand.registerAdapter();
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new BasicInventoryListener(), this);
        this.getServer().getPluginManager().registerEvents(new ColoredSignListener(), this);
        this.getServer().getPluginManager().registerEvents(new DisallowedCommandsListener(), this);
        this.getServer().getPluginManager().registerEvents(new FrozenPlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new FrozenServerListener(), this);
        this.getServer().getPluginManager().registerEvents(new GlintListener(), this);
        this.getServer().getPluginManager().registerEvents(new HeadNameListener(), this);
        this.getServer().getPluginManager().registerEvents(new TeleportationListener(), this);
        this.getServer().getPluginManager().registerEvents(new GSSCommand(), this);
        this.getServer().getPluginManager().registerEvents(new ChatFormatListener(), this);
        this.chatListener = new ChatListener();
        this.registerListener();
    }

    public void unregisterChat() {
        HandlerList.unregisterAll(this.chatListener);
    }

    public void setCustomChat(Listener listener) {
        if (this.chatListener != null) {
            HandlerList.unregisterAll(this.chatListener);
        }
        this.chatListener = listener;
    }

    public void registerListener() {
        this.getServer().getPluginManager().registerEvents(this.chatListener, this);
    }

    public static Basic getInstance() {
        return instance;
    }

    public String getNetworkWebsite() {
        return this.networkWebsite;
    }

    public String getTeamSpeakIp() {
        return this.teamSpeakIp;
    }

    public long getStartupTime() {
        return this.startupTime;
    }

    public ServerManager getServerManager() {
        return this.serverManager;
    }

    public MessagingManager getMessagingManager() {
        return this.messagingManager;
    }

    public ChatManager getChatManager() {
        return this.chatManager;
    }

    public HologramManager getHologramManager() {
        return this.hologramManager;
    }

    public Listener getChatListener() {
        return this.chatListener;
    }
}

