package net.lugami.qlib.lang;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Lang {

    @Getter private static final Map<JavaPlugin, Lang> langFiles = new HashMap<>();

    private YamlConfiguration config;
    private final File file;
    private final Map<String, Object> data = new HashMap<>();

    public Lang(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "lang.yml");
        if(!file.exists()) {
            InputStream configStream = plugin.getResource("lang.yml");
            if (configStream == null) {
                return;
            }
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(configStream);
            try {
                defConfig.save(this.file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lang.yml"));
        config.getConfigurationSection("lang").getKeys(false).forEach(s -> {
            data.put(s, config.get("lang." + s));
        });
        langFiles.put(plugin, this);

    }

    public String getString(String string, boolean color) {
        String conf = (String) data.get(string);
        return (color ? ChatColor.translateAlternateColorCodes('&', conf) : conf);
    }

    public String getString(String string) {
        return getString(string, true);
    }

    public List<String> getStringList(String string, boolean color) {
        return (color ? ((List<String>)data.get(string)).stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList()) : ((List<String>)data.get(string)));
    }

    public List<String> getStringList(String string) {
        return getStringList(string, true);
    }

    public Object get(String string) {
        return this.config.get(string);
    }

    public void set(String path, Object value) {
        this.config.set(path, value);
        try {
            this.config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
