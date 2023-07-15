
package net.lugami.bridge.global.status.threads;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.status.BridgeServer;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatusFetchThread extends Thread {

    public StatusFetchThread() {
        super("Bridge - Status Fetch Thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        while(BridgeGlobal.getJedisPool() != null && !BridgeGlobal.getJedisPool().isClosed()) {
            if(BridgeGlobal.isShutdown()) return;
            try (Jedis jedis = BridgeGlobal.getJedisPool().getResource()) {
                for (String keyName : jedis.keys("bridgeserver:*")) {
                    Map<String, String> data = jedis.hgetAll(keyName);
                    String name = keyName.split(":")[1];
                    long lastHeartbeat = Long.parseLong(data.get("lastHeartbeat"));
                    if((System.currentTimeMillis() - lastHeartbeat) > TimeUnit.MINUTES.toMillis(1)) {
                        System.out.println("[Bridge - Status Handler] Removing server: " + name + " from Redis and Bridge due to it being inactive for over a minute.");
                        deleteData(name);
                        continue;
                    }
                    BridgeServer bridgeServer = new BridgeServer(
                            name,
                            data.get("tab"),
                            data.get("group"),
                            data.get("status"),
                            data.get("motd"),
                            data.get("systemType"),
                            lastHeartbeat,
                            Long.parseLong(data.get("bootTime")),
                            Integer.parseInt(data.get("online")),
                            Integer.parseInt(data.get("maximum")),
                            Double.parseDouble(data.get("tps")),
                            new Gson().fromJson(data.get("users"), List.class)
                    );
                    if (data.containsKey("metadata"))
                        bridgeServer.setMetadata(new JsonParser().parse(data.get("metadata")).getAsJsonObject());
                    BridgeGlobal.getServerHandler().updateServer(name.toLowerCase(), bridgeServer);

                }
                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteData(String serverName) {
        try(Jedis jedis = BridgeGlobal.getJedisPool().getResource()) {
            jedis.del("bridgeserver:" + serverName);
            BridgeGlobal.getServerHandler().getServers().remove(serverName.toLowerCase());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}