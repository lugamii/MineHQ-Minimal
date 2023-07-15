package net.lugami.bridge;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.bukkit.listener.GeneralListener;
import net.lugami.bridge.bungee.BridgeBungee;
import net.lugami.bridge.bungee.listener.GeneralBungeeListener;
import net.lugami.bridge.global.disguise.DisguiseManager;
import net.lugami.bridge.global.handlers.*;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.status.ServerHandler;
import net.lugami.bridge.global.status.threads.StatusUpdateThread;
import net.lugami.bridge.global.updater.UpdaterManager;
import net.lugami.bridge.global.util.SystemType;
import net.lugami.bridge.global.handlers.PrefixHandler;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import net.lugami.bridge.global.handlers.*;

import java.util.*;

public class BridgeGlobal {

    @Getter @Setter public static SystemType systemType = SystemType.UNKNOWN;

    @Getter private static boolean mongoAuth, redisAuth, allowBannedJoins;

    @Getter private static int mongoPort, redisPort, redisDB;
    @Getter private static String mongoHost, mongoUsername, mongoPassword, mongoDatabase, redisHost, redisPassword, redisChannel, screenName, screenPath, serverName, serverDisplayName, serverWebsite, pluginUpdateDir ="%user_home%/Bridge/updater", groupName = "N/A";
    private static List<String> updaterGroups;
    private static String serverDeploymentDir;
    private static String serverTemplateDir;
    private static boolean handleServerDeployment;
    @Getter private static long startTime = -1L;
    @Getter private static MongoHandler mongoHandler;
    @Getter private static RankHandler rankHandler;
    @Getter private static PacketLogHandler packetLogHandler;
    @Getter private static PrefixHandler prefixHandler;
    @Getter private static ProfileHandler profileHandler;
    @Getter private static UpdaterManager updaterManager;
    @Getter private static FilterHandler filterHandler;
    @Getter private static ServerHandler serverHandler;
    @Getter private static JedisPool jedisPool;
    @Getter private static JsonParser parser = new JsonParser();
    @Getter private static DisguiseManager disguiseManager;

    @Getter @Setter private static boolean shutdown;

    public BridgeGlobal() {
        this("", "", "", "", 27017, false, "", "", "", 0, 6379, false, true);
    }

    public BridgeGlobal(String mongoHost, String mongoUsername, String mongoPassword, String mongoDatabase, int mongoPort, boolean mongoAuth, String redisHost, String redisPassword, String redisChannelKek, int redisDB, int redisPort, boolean redisAuth, boolean getFromConfig) {
        this(mongoHost, mongoUsername, mongoPassword, mongoDatabase, mongoPort, mongoAuth, redisHost, redisPassword, redisChannelKek, redisDB, redisPort, redisAuth, getFromConfig, null);
    }

    public BridgeGlobal(String mongoHost, String mongoUsername, String mongoPassword, String mongoDatabase, int mongoPort, boolean mongoAuth, String redisHost, String redisPassword, String redisChannelKek, int redisDB, int redisPort, boolean redisAuth, boolean getFromConfig, String customServerName) {
        setSystemType(currentSystemType());
        sendLog("We have detected that Bridge is running on System Type: " + getSystemType().name());

        if (customServerName != null)
            serverName = customServerName;

        if(getFromConfig) {
            setupConfigValues();
        }else {
            BridgeGlobal.mongoAuth = mongoAuth;
            BridgeGlobal.mongoPort = mongoPort;
            BridgeGlobal.mongoHost = mongoHost;
            BridgeGlobal.mongoUsername = mongoUsername;
            BridgeGlobal.mongoPassword = mongoPassword;
            BridgeGlobal.mongoDatabase = mongoDatabase;
            BridgeGlobal.redisAuth = redisAuth;
            BridgeGlobal.redisHost = redisHost;
            BridgeGlobal.redisPort = redisPort;
            BridgeGlobal.redisPassword = redisPassword;
            BridgeGlobal.redisChannel = redisChannelKek;
            BridgeGlobal.redisDB = redisDB;
        }
        sendLog("Loading Mongo backend...");
        mongoHandler = new MongoHandler();

        sendLog("Loading Ranks");
        rankHandler = new RankHandler();
        rankHandler.init();

        profileHandler = new ProfileHandler();
        profileHandler.init();
        sendLog("Successfully loaded the Profile Handler");

        prefixHandler = new PrefixHandler();
        prefixHandler.init();
        sendLog("Successfully loaded the Prefix Handler");

        packetLogHandler = new PacketLogHandler();
        sendLog("Successfully loaded the PacketLog Handler");

        filterHandler = new FilterHandler();
        filterHandler.init();
        sendLog("Successfully loaded the Filter Handler");

        jedisPool = new JedisPool(new JedisPoolConfig(), BridgeGlobal.getRedisHost(), BridgeGlobal.getRedisPort(), 0, (BridgeGlobal.isRedisAuth() ? BridgeGlobal.getRedisPassword() : null), BridgeGlobal.getRedisDB());
        PacketHandler.init(jedisPool, redisChannel);

//        sendLog("Setting up status handler");
//        StatusHandler.init();

        serverHandler = new ServerHandler();
        serverHandler.init();
        sendLog("Successfully setup the Server Handler");


        if(pluginUpdateDir != null && !pluginUpdateDir.isEmpty()) {
            sendLog("Setting up the update handler");
            updaterManager = new UpdaterManager();
        }
        startTime = System.currentTimeMillis();
        sendLog("Completed Bridge Backend - loaded " + getSystemType() + " system type.");
    }

    public static void loadDisguise(boolean bungee) {
        disguiseManager = new DisguiseManager(bungee);
        sendLog("Successfully loaded the Disguise Handler");
    }

    private void setupConfigValues() {
        mongoAuth = Boolean.parseBoolean(getFromConfig("mongo.auth.enabled"));
        mongoPort = Integer.parseInt(getFromConfig("mongo.port"));
        mongoHost = getFromConfig("mongo.host");
        mongoUsername = getFromConfig("mongo.auth.username");
        mongoPassword = getFromConfig("mongo.auth.password");
        mongoDatabase = getFromConfig("mongo.database");

        redisAuth = !getFromConfig("redis.password").equals("");
        redisHost = getFromConfig("redis.host");
        redisPort = Integer.parseInt(getFromConfig("redis.port"));
        redisPassword = getFromConfig("redis.password");
        redisChannel = getFromConfig("redis.channel");
        redisDB = Integer.parseInt(getFromConfig("redis.db"));
        screenName = getFromConfig("screenName");
        screenPath = getFromConfig("screenPath");
        serverName = getFromConfig("serverName");
        allowBannedJoins = Boolean.parseBoolean(getFromConfig("AllowBannedJoins"));
        serverDisplayName = getFromConfig("serverDisplayName");
        serverWebsite = getFromConfig("serverWebsite");
        pluginUpdateDir = getFromConfig("pluginUpdateDirectory");
        groupName = getFromConfig("serverGroup");
        handleServerDeployment = Boolean.parseBoolean(BridgeGlobal.getFromConfig("handleServerDeployment"));
        serverDeploymentDir = BridgeGlobal.getFromConfig("serverDeploymentDirectory");
        serverTemplateDir = BridgeGlobal.getFromConfig("serverTemplateDirectory");
        if(getSystemType() == SystemType.BUKKIT) updaterGroups = Bridge.getInstance().getConfig().getStringList("updaterGroups");
    }

    public static void shutdown() {
        shutdown = true;
        StatusUpdateThread.saveShutdown();
        getProfileHandler().saveDisable();
        getRankHandler().saveDisable();
        getMongoHandler().getMongoClient().close();
        getJedisPool().close();
    }

    private static SystemType currentSystemType() {
        try {
            Class.forName("net.md_5.bungee.BungeeCord");
            return SystemType.BUNGEE;
        } catch (ClassNotFoundException ignored) {}
        try {
            Class.forName("org.bukkit.Bukkit");
            return SystemType.BUKKIT;
        } catch (ClassNotFoundException ignored) {}
        return SystemType.UNKNOWN;
    }

    public static String getSystemName() {
        switch (getSystemType()) {
            case BUNGEE:
            case BUKKIT: {
                return serverName;
            }
            default: {
                if (serverName != null && !serverName.isEmpty())
                    return serverName;

                return "Custom Java Applet";
            }
        }
    }

    public static void sendLog(String msg, boolean packetIncoming) {
        String logMessage = "%prefix% §r" + msg;
        switch (systemType) {
            case BUNGEE:
                GeneralBungeeListener.logMessages(msg.replace("%prefix%", "[Bridge]"));
                return;
            case BUKKIT:
                GeneralListener.logMessages(msg.replace("%prefix%", "[Bridge]"), packetIncoming);
                return;
            default:
                System.out.println(logMessage.replace("%prefix%", "[Bridge]").replace("§", ""));
        }
    }

    public static void sendLog(String msg) {
        sendLog(msg, false);
    }


    public static String getFromConfig(String path) {
        switch(systemType) {
            case BUNGEE: {
                return BridgeBungee.getConfig().getString(path);
            }
            case UNKNOWN: {
                return null;
            }
            case BUKKIT: {
                return Bridge.getInstance().getConfig().getString(path);
            }
        }
        return null;
    }

    public static List<String> getUpdaterGroups() {
        if(getSystemType() != SystemType.BUKKIT) return null;
        List<String> list = new ArrayList<>(Collections.singletonList(getGroupName()));
        if(!updaterGroups.isEmpty()) list.addAll(updaterGroups);
        return list;
    }

    public static boolean isHandleServerDeployment() {
        return handleServerDeployment;
    }

    public static void setHandleServerDeployment(boolean handleServerDeployment) {
        BridgeGlobal.handleServerDeployment = handleServerDeployment;
    }

    public static String getServerDeploymentDir() {
        return serverDeploymentDir.replace("%user_home%", System.getProperty("user.home"));
    }

    public static void setServerDeploymentDir(String serverDeploymentDir) {
        BridgeGlobal.serverDeploymentDir = serverDeploymentDir;
    }

    public static String getServerTemplateDir() {
        return serverTemplateDir.replace("%user_home%", System.getProperty("user.home"));
    }

    public static void setServerTemplateDir(String serverTemplateDir) {
        BridgeGlobal.serverTemplateDir = serverTemplateDir;
    }

    public static void addUpdaterGroup(String group) {
        updaterGroups.add(group);
    }

    public static void removeUpdaterGroup(String group) {
        updaterGroups.remove(group);
    }

    public static void savePlayerData(String key, UUID uuid, JsonObject data) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(key, uuid.toString(), data.toString());
        }
    }

    public static void deletePlayerData(String key, UUID uuid) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel(key, uuid.toString());
        }
    }

    public static JsonObject getPlayerData(UUID uuid, String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String data = jedis.hget(key, uuid.toString());

            if(data == null) {
                return null;
            }

            try {
                JsonObject object = parser.parse(data).getAsJsonObject();

                if(object.has("data")) {
                    return object.get("data").getAsJsonObject();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static List<String> getUsedDisguisedNames() {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> data = jedis.hgetAll("disguise");

            if(data == null) {
                return null;
            }

            List<String> names = Lists.newArrayList();

            for(String dataStructure : data.values()) {
                JsonObject object = parser.parse(dataStructure).getAsJsonObject();

                if(object.has("data")) {
                    JsonObject values = object.get("data").getAsJsonObject();

                    if(values.has("disguiseName")) {
                        names.add(values.get("disguiseName").getAsString());
                    }
                }
            }

            return names;
        }
    }
}
