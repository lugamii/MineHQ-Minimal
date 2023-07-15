package net.lugami.bridge.global.status.start;

import lombok.AllArgsConstructor;
import net.lugami.bridge.global.status.BridgeServer;

import java.io.IOException;

@AllArgsConstructor
public class ServerStartThread extends Thread {

    private BridgeServer server;

    @Override
    public void run() {
        try {
            runInScreen(server.getScreenName(), "./start.sh");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Process runInScreen(String sessionName, String command) throws IOException {
        return new ProcessBuilder("screen", "-DmS", sessionName, "bash", "-c", command).inheritIO().start();
    }
}