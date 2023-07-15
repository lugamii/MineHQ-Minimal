package net.lugami.qlib.serialization;

import com.google.gson.*;
import org.bukkit.util.BlockVector;

import java.lang.reflect.Type;

public class BlockVectorAdapter implements JsonDeserializer<BlockVector>, JsonSerializer<BlockVector> {

    public BlockVector deserialize(JsonElement src, Type type, JsonDeserializationContext context) throws JsonParseException {
        return BlockVectorAdapter.fromJson(src);
    }

    public JsonElement serialize(BlockVector src, Type type, JsonSerializationContext context) {
        return BlockVectorAdapter.toJson(src);
    }

    public static JsonObject toJson(BlockVector src) {
        if (src == null) {
            return null;
        }
        JsonObject object = new JsonObject();
        object.addProperty("x", src.getX());
        object.addProperty("y", src.getY());
        object.addProperty("z", src.getZ());
        return object;
    }

    public static BlockVector fromJson(JsonElement src) {
        if (src == null || !src.isJsonObject()) {
            return null;
        }
        JsonObject json = src.getAsJsonObject();
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        return new BlockVector(x, y, z);
    }
}

