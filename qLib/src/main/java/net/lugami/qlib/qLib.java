package net.lugami.qlib;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.lugami.qlib.autoreboot.AutoRebootHandler;
import net.lugami.qlib.boss.FrozenBossBarHandler;
import net.lugami.qlib.chat.ChatHandler;
import net.lugami.qlib.command.FrozenCommandHandler;
import net.lugami.qlib.economy.FrozenEconomyHandler;
import net.lugami.qlib.event.HalfHourEvent;
import net.lugami.qlib.event.HourEvent;
import net.lugami.qlib.hologram.FrozenHologramHandler;
import net.lugami.qlib.hologram.listener.HologramListener;
import net.lugami.qlib.nametag.FrozenNametagHandler;
import net.lugami.qlib.protocol.InventoryAdapter;
import net.lugami.qlib.protocol.LagCheck;
import net.lugami.qlib.protocol.PingAdapter;
import net.lugami.qlib.redis.RedisCommand;
import net.lugami.qlib.redstone.RedstoneListener;
import net.lugami.qlib.scoreboard.FrozenScoreboardHandler;
import net.lugami.qlib.serialization.*;
import net.lugami.qlib.tab.FrozenTabHandler;
import net.lugami.qlib.tab.TabAdapter;
import net.lugami.qlib.util.ItemUtils;
import net.lugami.qlib.util.SignGUI;
import net.lugami.qlib.util.TPSUtils;
import net.lugami.qlib.uuid.FrozenUUIDCache;
import net.lugami.qlib.visibility.FrozenVisibilityHandler;
import net.lugami.qlib.xpacket.FrozenXPacketHandler;
import net.lugami.qlib.serialization.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class qLib extends JavaPlugin {

    private static qLib instance;
    private long localRedisLastError;
    private long backboneRedisLastError;
    public static boolean testing;
    @Getter private static boolean bridge;
    public static final Random RANDOM;
    public static final Gson GSON;
    public static final Gson PLAIN_GSON;
    @Getter private SignGUI signGUI;
    private JedisPool localJedisPool;
    private JedisPool backboneJedisPool;

    public void onEnable() {
        instance = this;
        testing = this.getConfig().getBoolean("testing", false);
        this.saveDefaultConfig();

        try {
            this.localJedisPool = new JedisPool(new JedisPoolConfig(), this.getConfig().getString("Redis.Host"), 6379, 20000, (this.getConfig().getString("Redis.Password").equals("") ? null : this.getConfig().getString("Redis.Password")), this.getConfig().getInt("Redis.DbId", 0));
        }
        catch (Exception e) {
            this.localJedisPool = null;
            e.printStackTrace();
            this.getLogger().warning("Couldn't connect to a Redis instance at " + this.getConfig().getString("Redis.Host") + ".");
        }
        try {
            this.backboneJedisPool = new JedisPool(new JedisPoolConfig(), this.getConfig().getString("BackboneRedis.Host"), 6379, 20000, (this.getConfig().getString("BackboneRedis.Password").equals("") ? null : this.getConfig().getString("BackboneRedis.Password")), this.getConfig().getInt("BackboneRedis.DbId", 0));
        }
        catch (Exception e) {
            this.backboneJedisPool = null;
            e.printStackTrace();
            this.getLogger().warning("Couldn't connect to a Backbone Redis instance at " + this.getConfig().getString("BackboneRedis.Host") + ".");
        }
        bridge = this.getConfig().getBoolean("Bridge", true);
        FrozenCommandHandler.init();
        FrozenNametagHandler.init();
        FrozenScoreboardHandler.init();
        FrozenUUIDCache.init();
        FrozenXPacketHandler.init();
        FrozenBossBarHandler.init();
        FrozenTabHandler.init();
        AutoRebootHandler.init();
        FrozenVisibilityHandler.init();
        ChatHandler.init();
        FrozenHologramHandler.init();
        FrozenCommandHandler.registerAll(this);
        FrozenCommandHandler.registerPackage(this, "net.lugami.qlib.chat.commands");
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPSUtils(), 1L, 1L);
        ItemUtils.load();
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        new BukkitRunnable(){

            public void run() {
                if (Bukkit.getPluginManager().getPlugin("CommonLibs") != null || Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
                    ProtocolLibrary.getProtocolManager().addPacketListener(new InventoryAdapter());
                    PingAdapter ping = new PingAdapter();
                    ProtocolLibrary.getProtocolManager().addPacketListener(ping);
                    Bukkit.getPluginManager().registerEvents(ping, qLib.getInstance());
                    new LagCheck().runTaskTimerAsynchronously(qLib.this, 100L, 100L);
                    ProtocolLibrary.getProtocolManager().addPacketListener(new TabAdapter());
                    Bukkit.getPluginManager().registerEvents(new HologramListener(), qLib.this);
                }
            }
        }.runTaskLater(this, 1L);
        Bukkit.getPluginManager().registerEvents(new RedstoneListener(), this);
        this.setupHourEvents();
        signGUI = new SignGUI(this);
    }

    public void onDisable() {
        if (FrozenEconomyHandler.isInitiated()) {
            FrozenEconomyHandler.saveAll();
        }
        this.localJedisPool.close();
        this.backboneJedisPool.close();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T runRedisCommand(RedisCommand<T> redisCommand) {
        if (testing) {
            return null;
        }
        Jedis jedis = this.localJedisPool.getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.localRedisLastError = System.currentTimeMillis();
            if (jedis != null) {
                this.localJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally {
            if (jedis != null) {
                this.localJedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T runBackboneRedisCommand(RedisCommand<T> redisCommand) {
        if (testing) {
            return null;
        }
        Jedis jedis = this.backboneJedisPool.getResource();
        T result = null;
        try {
            result = redisCommand.execute(jedis);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.backboneRedisLastError = System.currentTimeMillis();
            if (jedis != null) {
                this.backboneJedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
        }
        finally {
            if (jedis != null) {
                this.backboneJedisPool.returnResource(jedis);
            }
        }
        return result;
    }

    @Deprecated
    public long getLocalRedisLastError() {
        return this.localRedisLastError;
    }

    @Deprecated
    public long getBackboneRedisLastError() {
        return this.backboneRedisLastError;
    }

    private void setupHourEvents() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("qLib - Hour Event Thread").setDaemon(true).build());
        int minOfHour = Calendar.getInstance().get(12);
        int minToHour = 60 - minOfHour;
        int minToHalfHour = minToHour >= 30 ? minToHour : 30 - minOfHour;
        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new HourEvent(Calendar.getInstance().get(11)))), minToHour, 60L, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().callEvent(new HalfHourEvent(Calendar.getInstance().get(11), Calendar.getInstance().get(12)))), minToHalfHour, 30L, TimeUnit.MINUTES);
    }

    public static qLib getInstance() {
        return instance;
    }

    public JedisPool getLocalJedisPool() {
        return this.localJedisPool;
    }

    public JedisPool getBackboneJedisPool() {
        return this.backboneJedisPool;
    }


    static {
        testing = false;
        RANDOM = new Random();
        GSON = new GsonBuilder().registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter()).registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationAdapter()).registerTypeHierarchyAdapter(Vector.class, new VectorAdapter()).registerTypeAdapter(BlockVector.class, new BlockVectorAdapter()).setPrettyPrinting().serializeNulls().create();
        PLAIN_GSON = new GsonBuilder().registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter()).registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter()).registerTypeHierarchyAdapter(Location.class, new LocationAdapter()).registerTypeHierarchyAdapter(Vector.class, new VectorAdapter()).registerTypeAdapter(BlockVector.class, new BlockVectorAdapter()).serializeNulls().create();
    }

}

