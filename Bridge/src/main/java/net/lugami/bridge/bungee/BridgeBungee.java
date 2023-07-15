package net.lugami.bridge.bungee;

import net.lugami.bridge.bungee.implement.BungeeImplementer;
import net.lugami.bridge.bungee.listener.BridgeBungeeListener;
import net.lugami.bridge.BridgeGlobal;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class BridgeBungee extends Plugin {

    @Getter private static BridgeBungee instance;
    @Getter private static Configuration config;
    @Getter private File configFile;

    @Override
    public void onEnable() {
        instance = this;
        setupConfig();

        BridgeGlobal.loadDisguise(true);

        BungeeCord.getInstance().getPluginManager().registerListener(this, new BridgeBungeeListener());
        BridgeGlobal.getServerHandler().registerProvider(new BungeeImplementer());
    }

    @Override
    public void onDisable() {
        BridgeGlobal.shutdown();
        instance = null;
    }


    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setupConfig() {
        System.out.println("oof");
        configFile = new File(getDataFolder(), "config.yml");
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }
        if(config != null) {
            new BridgeGlobal();
        }
    }

}
