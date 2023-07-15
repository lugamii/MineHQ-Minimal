package net.lugami.qlib.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class NetworkUtils {

    private NetworkUtils() {
    }

    public static void writeVarInt(DataOutputStream out, int value) throws IOException {
        for (int i = 0; i < 3; ++i) {
            if ((value & -128) == 0) {
                out.write(value);
                return;
            }
            out.write(value & 127 | 128);
            value >>>= 7;
        }
    }

    public static void writeString(DataOutputStream out, String str) throws IOException {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        NetworkUtils.writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public static void writePacket(DataOutputStream out, byte[] bytes) throws IOException {
        NetworkUtils.writeVarInt(out, bytes.length);
        out.write(bytes);
    }

    public static int readVarInt(DataInputStream in) throws IOException {
        int value = 0;
        for (int i = 0; i < 3; ++i) {
            int b = in.read();
            value |= (b & 127) << i * 7;
            if ((b & 128) == 0) break;
        }
        return value;
    }

    public static String readString(DataInputStream in) throws IOException {
        int len = NetworkUtils.readVarInt(in);
        byte[] bytes = new byte[len];
        in.readFully(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] readPacket(DataInputStream in) throws IOException {
        int len = NetworkUtils.readVarInt(in);
        byte[] bytes = new byte[len];
        in.readFully(bytes);
        return bytes;
    }
}

