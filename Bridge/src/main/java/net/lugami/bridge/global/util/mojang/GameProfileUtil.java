package net.lugami.bridge.global.util.mojang;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lugami.bridge.BridgeGlobal;
import lombok.Getter;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameProfileUtil {

    @Getter
    private static final Map<String, GameProfile> skinCache = Maps.newHashMap();

    public static GameProfile loadGameProfile(UUID uniqueId, String skinName) {
        GameProfile profile = skinCache.get(skinName.toLowerCase());

        BufferedReader reader = null;
        try {
            if(profile == null || !profile.getProperties().containsKey("textures")) {
                URL url = new URL( "https://sessionserver.mojang.com/session/minecraft/profile/" + uniqueId.toString().replace("-", "") + "?unsigned=false");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.addRequestProperty("User-Agent", "Core");
                connection.setDoOutput(true);
                connection.connect();

                if(connection.getResponseCode() == 200) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String line;
                    List<String> lines = Lists.newArrayList();

                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }

                    String response = Joiner.on("\n").join(lines);

                    JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                    skinName = object.get("name").getAsString();

                    if(profile == null) {
                        profile = new GameProfile(uniqueId, skinName);
                    }

                    JsonArray array = object.get("properties").getAsJsonArray();
                    for(Object obj : array) {
                        JsonObject property = (JsonObject) obj;
                        String propertyName = property.get("name").getAsString();

                        profile.getProperties().put(propertyName, new Property(propertyName, property.get("value").getAsString(), property.get("signature").getAsString()));
                    }

                    skinCache.put(skinName.toLowerCase(), profile);
                    MinecraftServer.getServer().getUserCache().a(profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {

                }
            }
        }

        return profile;
    }

    public static GameProfile setName(GameProfile gameProfile, String newName) {
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");

            AccessController.doPrivileged((PrivilegedAction) () -> {
                modifiersField.setAccessible(true);
                return null;
            });

            Field nameField = GameProfile.class.getDeclaredField("name");
            modifiersField.setInt(nameField, nameField.getModifiers() & ~Modifier.FINAL);
            nameField.setAccessible(true);
            nameField.set(gameProfile, newName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gameProfile;
    }

    public static GameProfile clone(GameProfile gameProfile) {
        GameProfile newProfile = new GameProfile(gameProfile.getId(), gameProfile.getName());
        newProfile.getProperties().putAll(gameProfile.getProperties());
        return newProfile;
    }

    public static String getRealName(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonElement element = BridgeGlobal.getParser().parse(reader);

            if (element != null && element.isJsonObject()) {
                String realName = element.getAsJsonObject().get("name").getAsString();

                if(realName != null) {
                    return realName;
                }
            }
        } catch (IOException ignored) {}

        return null;
    }
}
