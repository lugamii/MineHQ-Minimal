package net.lugami.practice.chat;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import net.lugami.qlib.qLib;
import net.lugami.qlib.redis.RedisCommand;
import net.lugami.qlib.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import net.lugami.practice.Practice;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PersistMap<T> {

    protected Map<UUID, T> wrappedMap = new ConcurrentHashMap<>();

    private String keyPrefix;
    private String mongoName;
    private boolean useMongo;

    public PersistMap(String keyPrefix, String mongoName) {
        this(keyPrefix, mongoName, true); // use mongo by default
    }

    public PersistMap(String keyPrefix, String mongoName, boolean useMongo) {
        this.keyPrefix = keyPrefix;
        this.mongoName = mongoName;
        this.useMongo = useMongo;

        loadFromRedis();
    }

    public void loadFromRedis() {
        qLib.getInstance().runBackboneRedisCommand(redis -> {
            Map<String, String> results = redis.hgetAll(Bukkit.getServerName() + ":" + keyPrefix);

            for (Map.Entry<String, String> resultEntry : results.entrySet()) {
                T object = getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());

                if (object != null) {
                    wrappedMap.put(UUID.fromString(resultEntry.getKey()), object);
                }
            }

            return (null);
        });
    }

    protected void wipeValues() {
        wrappedMap.clear();

        qLib.getInstance().runBackboneRedisCommand(redis -> {
            redis.del(Bukkit.getServerName() + ":" + keyPrefix);
            return (null);
        });
    }

    protected void updateValueSync(final UUID key, final T value) {
        wrappedMap.put(key, value);

        qLib.getInstance().runBackboneRedisCommand((RedisCommand<T>) redis -> {
            redis.hset(Bukkit.getServerName() + ":" + keyPrefix, key.toString(), getRedisValue(getValue(key)));

            boolean succeeded = false;
            int tries = 0;
            while (useMongo && !succeeded && tries++ < 5) {
                try {
                    DBCollection playersCollection = (DBCollection) Practice.getInstance().getMongoDatabase().getCollection("Players");
                    BasicDBObject player = new BasicDBObject("_id", key.toString().replace("-", ""));

                    BasicDBObject toSet = new BasicDBObject(mongoName, getMongoValue(getValue(key)));
                    toSet.put("lastUsername", UUIDUtils.name(key));

                    playersCollection.update(player, new BasicDBObject("$set", toSet), true, false);
                    succeeded = true;
                } catch (Exception e) {
                    succeeded = false;
                }
            }

            if (useMongo && !succeeded) {
                throw new RuntimeException("Mongo insert failed after five retries! User: " + key + ".");
            }
            return (null);
        });
    }

    protected void updateValueAsync(final UUID key, T value) {
        wrappedMap.put(key, value);

        new BukkitRunnable() {

            public void run() {
                qLib.getInstance().runBackboneRedisCommand(redis -> {
                    redis.hset(Bukkit.getServerName() + ":" + keyPrefix, key.toString(), getRedisValue(getValue(key)));

                    boolean succeeded = false;
                    int tries = 0;
                    while (useMongo && !succeeded && tries++ < 5) {
                        try {
                            DBCollection playersCollection = (DBCollection) Practice.getInstance().getMongoDatabase().getCollection("Players");
                            BasicDBObject player = new BasicDBObject("_id", key.toString().replace("-", ""));

                            BasicDBObject toSet = new BasicDBObject(mongoName, getMongoValue(getValue(key)));
                            toSet.put("lastUsername", UUIDUtils.name(key));

                            playersCollection.update(player, new BasicDBObject("$set", toSet), true, false);
                            succeeded = true;
                        } catch (Exception e) {
                            succeeded = false;
                        }
                    }

                    if (useMongo && !succeeded) {
                        throw new RuntimeException("Mongo update failed after five retries! User: " + key + ".");
                    }
                    return (null);
                });
            }

        }.runTaskAsynchronously(Practice.getInstance());
    }

    protected T getValue(UUID key) {
        return (wrappedMap.get(key));
    }

    protected boolean contains(UUID key) {
        return (wrappedMap.containsKey(key));
    }

    public abstract String getRedisValue(T t);

    public abstract Object getMongoValue(T t);

    public T getJavaObjectSafe(String key, String redisValue) {
        try {
            return (getJavaObject(redisValue));
        } catch (Exception e) {
            System.out.println("Error parsing Redis result.");
            System.out.println(" - Prefix: " + keyPrefix);
            System.out.println(" - Key: " + key);
            System.out.println(" - Value: " + redisValue);
            return (null);
        }
    }

    public abstract T getJavaObject(String str);

}