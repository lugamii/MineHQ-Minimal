package net.lugami.bridge.global.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MojangUtils {

    public static UUID fetchUUID(String playerName) throws Exception {
        URL url = new URL("https://api.minetools.eu/uuid/" + playerName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        JsonElement element = new JsonParser().parse(bufferedReader);
        JsonObject object = element.getAsJsonObject();
        String status = object.get("status").getAsString();
        if(status.equalsIgnoreCase("ERR")) {
            return null;
        }

        String uuidAsString = object.get("id").getAsString();

        return parseUUIDFromString(uuidAsString);
    }

    public static String fetchName(UUID uuid) throws Exception {
        URL url = new URL("https://api.minetools.eu/profile/" + uuid.toString().replace("-", ""));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        JsonElement element = new JsonParser().parse(bufferedReader);
        JsonObject object = element.getAsJsonObject();
        JsonElement raw = object.get("raw");
        JsonObject rawObject = raw.getAsJsonObject();
        String status = rawObject.get("status").getAsString();
        if(status == null) {
            return null;
        }
        return rawObject.get("name").getAsString();

    }

    private static UUID parseUUIDFromString(String uuidAsString) {
        String[] parts = {
                "0x" + uuidAsString.substring(0, 8),
                "0x" + uuidAsString.substring(8, 12),
                "0x" + uuidAsString.substring(12, 16),
                "0x" + uuidAsString.substring(16, 20),
                "0x" + uuidAsString.substring(20, 32)
        };

        long mostSigBits = Long.decode(parts[0]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[1]);
        mostSigBits <<= 16;
        mostSigBits |= Long.decode(parts[2]);

        long leastSigBits = Long.decode(parts[3]);
        leastSigBits <<= 48;
        leastSigBits |= Long.decode(parts[4]);

        return new UUID(mostSigBits, leastSigBits);
    }

}