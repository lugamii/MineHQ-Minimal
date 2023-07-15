package net.lugami.qlib.serialization;

import java.lang.reflect.Type;

import com.google.gson.*;
import net.lugami.qlib.qLib;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationAdapter implements JsonDeserializer<Location>, JsonSerializer<Location> {

    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        return LocationAdapter.toJson(src);
    }

    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return LocationAdapter.fromJson(json);
    }

    public static JsonObject toJson(Location location) {
        if (location == null) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("world", location.getWorld().getName());
        jsonObject.addProperty("x", location.getX());
        jsonObject.addProperty("y", location.getY());
        jsonObject.addProperty("z", location.getZ());
        jsonObject.addProperty("yaw", location.getYaw());
        jsonObject.addProperty("pitch", location.getPitch());
        return jsonObject;
    }

    public static Location fromJson(JsonElement jsonElement) {
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return null;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        World world = qLib.getInstance().getServer().getWorld(jsonObject.get("world").getAsString());
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float yaw = jsonObject.get("yaw").getAsFloat();
        float pitch = jsonObject.get("pitch").getAsFloat();
        return new Location(world, x, y, z, yaw, pitch);
    }
}

