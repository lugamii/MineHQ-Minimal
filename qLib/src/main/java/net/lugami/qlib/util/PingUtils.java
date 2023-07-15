package net.lugami.qlib.util;

import java.beans.ConstructorProperties;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import net.lugami.qlib.qLib;

public final class PingUtils {

    private PingUtils() {
    }

    public static void ping(String host, int port, Callback callback) {
        new PingTask(host, port, callback).run();
    }

    public interface Callback {
        void success(PingResponse var1);

        void failure(Exception var1);
    }

    private static class PingTask
    implements Runnable {
        private final String host;
        private final int port;
        private final Callback callback;

        @Override
        public void run() {
            try {
                try (Socket socket = new Socket()){
                    InetSocketAddress address = new InetSocketAddress(this.host, this.port);
                    socket.connect(address, 5000);
                    socket.setSoTimeout(5000);
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    ByteArrayOutputStream handshake = new ByteArrayOutputStream();
                    DataOutputStream handshakeOut = new DataOutputStream(handshake);
                    NetworkUtils.writeVarInt(handshakeOut, 0);
                    NetworkUtils.writeVarInt(handshakeOut, 4);
                    NetworkUtils.writeString(handshakeOut, this.host);
                    handshakeOut.writeShort(this.port);
                    NetworkUtils.writeVarInt(handshakeOut, 1);
                    NetworkUtils.writePacket(out, handshake.toByteArray());
                    ByteArrayOutputStream status = new ByteArrayOutputStream();
                    DataOutputStream statusOut = new DataOutputStream(status);
                    NetworkUtils.writeVarInt(statusOut, 0);
                    NetworkUtils.writePacket(out, status.toByteArray());
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    byte[] response = NetworkUtils.readPacket(in);
                    DataInputStream responseIn = new DataInputStream(new ByteArrayInputStream(response));
                    int id = NetworkUtils.readVarInt(responseIn);
                    if (id != 0) {
                        throw new Exception("Unexpected packet ID");
                    }
                    String jsonResponse = NetworkUtils.readString(responseIn);
                    this.callback.success(qLib.GSON.fromJson(jsonResponse, PingResponse.class));
                }
            }
            catch (Exception e) {
                this.callback.failure(e);
            }
        }

        @ConstructorProperties(value={"host", "port", "callback"})
        public PingTask(String host, int port, Callback callback) {
            this.host = host;
            this.port = port;
            this.callback = callback;
        }
    }

    public static class PingResponse {
        private Version version;
        private Players players;
        private String description;
        private String favicon;

        public Version getVersion() {
            return this.version;
        }

        public Players getPlayers() {
            return this.players;
        }

        public String getDescription() {
            return this.description;
        }

        public String getFavicon() {
            return this.favicon;
        }

        public static class Version {
            private String name;
            private int protocol;

            public String getName() {
                return this.name;
            }

            public int getProtocol() {
                return this.protocol;
            }
        }

        public static class Players {
            private int max;
            private int online;

            public int getMax() {
                return this.max;
            }

            public int getOnline() {
                return this.online;
            }
        }

    }

}

