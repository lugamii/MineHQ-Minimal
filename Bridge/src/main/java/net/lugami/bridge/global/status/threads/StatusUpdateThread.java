package net.lugami.bridge.global.status.threads;

import com.google.gson.Gson;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.status.BridgeServer;
import net.lugami.bridge.global.status.StatusProvider;
import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

public class StatusUpdateThread extends Thread {

    public StatusUpdateThread() {
        super("Bridge - Status Update Thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        while(BridgeGlobal.getJedisPool() != null && !BridgeGlobal.getJedisPool().isClosed()) {
            try(Jedis jedis = BridgeGlobal.getJedisPool().getResource()) {
                if(BridgeGlobal.isShutdown()) return;
                BridgeServer bridgeServer = BridgeGlobal.getServerHandler().getServer();
                StatusProvider statusProvider = bridgeServer.getStatusProvider();
                bridgeServer.setProvider(statusProvider.getName());
                bridgeServer.setGroup(BridgeGlobal.getGroupName());
                bridgeServer.setStatus(statusProvider.serverStatus());
                bridgeServer.setMotd(statusProvider.motd());
                bridgeServer.setLastHeartbeat(System.currentTimeMillis());
                bridgeServer.setBootTime(BridgeGlobal.getStartTime());
                bridgeServer.setOnline(statusProvider.online());
                bridgeServer.setMaximum(statusProvider.maximum());
                bridgeServer.setTps(statusProvider.tps());
                bridgeServer.setUsers(statusProvider.players());
                bridgeServer.setSystemType(BridgeGlobal.getSystemType().name());
                if(statusProvider.dataPassthrough() != null) bridgeServer.setMetadata(statusProvider.dataPassthrough());

                BridgeGlobal.getServerHandler().updateServer(bridgeServer.getName(), bridgeServer);

                Gson gson = new Gson();
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "tab", bridgeServer.getStatusProvider().getName());
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "group", bridgeServer.getGroup());
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "status", bridgeServer.getStatus());
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "motd", bridgeServer.getMotd());
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "systemType", bridgeServer.getSystemType());
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "lastHeartbeat", String.valueOf(bridgeServer.getLastHeartbeat()));
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "bootTime", String.valueOf(BridgeGlobal.getStartTime()));
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "online", String.valueOf(bridgeServer.getOnline()));
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "maximum", String.valueOf(bridgeServer.getMaximum()));
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "users", gson.toJson(bridgeServer.getUsers()));
                jedis.hset("bridgeserver:" + bridgeServer.getName(), "tps", String.valueOf(bridgeServer.getTps()));
                if(bridgeServer.getMetadata() != null) jedis.hset("bridgeserver:" + bridgeServer.getName(), "metadata", bridgeServer.getMetadata().toString());

                Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveShutdown() {
        BridgeServer bridgeServer = BridgeGlobal.getServerHandler().getServer();
        try(Jedis jedis = BridgeGlobal.getJedisPool().getResource()) {
            jedis.hset("bridgeserver:" + bridgeServer.getName(), "status", "OFFLINE");
            jedis.hset("bridgeserver:" + bridgeServer.getName(), "online", String.valueOf(0));
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}