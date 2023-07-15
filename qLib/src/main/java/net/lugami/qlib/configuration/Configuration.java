package net.lugami.qlib.configuration;

import net.lugami.qlib.configuration.annotations.ConfigData;
import net.lugami.qlib.configuration.annotations.ConfigSerializer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Configuration {

    private final YamlConfiguration config;
    private final File file;
    private final File directory;

    public Configuration(JavaPlugin plugin) {
        this(plugin, "config.yml");
    }

    public Configuration(JavaPlugin plugin, String filename) {
        this(plugin, filename, plugin.getDataFolder().getPath());
    }

    public Configuration(JavaPlugin plugin, String filename, String directory) {
        this.directory = new File(directory);
        this.file = new File(directory, filename);
        this.config = new YamlConfiguration();
        this.createFile();
    }

    public void createFile() {
        if (!this.directory.exists()) {
            this.directory.mkdirs();
        }
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.config.load(this.file);
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        Field[] toSave;
        for (Field f : toSave = this.getClass().getDeclaredFields()) {
            if (!f.isAnnotationPresent(ConfigData.class)) continue;
            ConfigData configData = f.getAnnotation(ConfigData.class);
            try {
                f.setAccessible(true);
                Object saveValue = f.get(this);
                Object configValue = null;
                if (f.isAnnotationPresent(ConfigSerializer.class)) {
                    ConfigSerializer serializer = f.getAnnotation(ConfigSerializer.class);
                    if (saveValue instanceof List) {
                        configValue = new ArrayList<>();
                        for (Object o : (List)configValue) {
                            AbstractSerializer as = (AbstractSerializer)serializer.serializer().newInstance();
                            ((List)configValue).add(as.toString(o));
                        }
                    } else {
                        AbstractSerializer as = (AbstractSerializer)serializer.serializer().newInstance();
                        configValue = as.toString(saveValue);
                    }
                } else if (saveValue instanceof List) {
                    configValue = new ArrayList<>();
                    for (Object o : (List)saveValue) {
                        ((List)configValue).add(o.toString());
                    }
                }
                if (configValue == null) {
                    configValue = saveValue;
                }
                this.config.addDefault(configData.path(), configValue);
                this.config.set(configData.path(), configValue);
            }
            catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        try {
            this.config.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Field[] toLoad;
        for (Field f : toLoad = this.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (!f.isAnnotationPresent(ConfigData.class)) continue;
            ConfigData configData = f.getAnnotation(ConfigData.class);
            if (!this.config.contains(configData.path())) continue;
            f.setAccessible(true);
            if (!f.isAnnotationPresent(ConfigSerializer.class)) {
                try {
                    if (this.config.isList(configData.path())) {
                        f.set(this, this.config.getList(configData.path()));
                        continue;
                    }
                    f.set(this, this.config.get(configData.path()));
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (this.config.isList(configData.path())) {
                try {
                    List<String> list = this.config.getStringList(configData.path());
                    ArrayList<Object> deserializedList = new ArrayList<Object>();
                    for (String s : list) {
                        deserializedList.add(this.deserializeValue(f, s));
                    }
                    f.set(this, deserializedList);
                }
                catch (IllegalAccessException | InstantiationException e) {
                    System.out.println("Error reading list in configuration file: " + this.config.getName() + " path: " + configData.path());
                    e.printStackTrace();
                }
                continue;
            }
            try {
                Object object = this.config.get(configData.path());
                f.set(this, this.deserializeValue(f, object.toString()));
            }
            catch (IllegalAccessException | InstantiationException e) {
                System.out.println("Error reading value in configuration file: " + this.config.getName() + " path: " + configData.path());
                e.printStackTrace();
            }
        }
    }

    public File getFile() {
        return this.file;
    }

    public Object deserializeValue(Field f, Object value) throws IllegalAccessException, InstantiationException {
        AbstractSerializer serializer = (AbstractSerializer)f.getAnnotation(ConfigSerializer.class).serializer().newInstance();
        return serializer.fromString(value.toString());
    }
}

