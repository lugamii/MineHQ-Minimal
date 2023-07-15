package net.lugami.bridge.global.disguise;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bson.Document;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisguisePlayerSkin {

    private UUID profileUuid;
    private Property property;

    public static void toJson(Document document, String type, DisguisePlayerSkin skin) {
        document.put(type + "ProfileUuid", skin.getProfileUuid().toString());

        Property property = skin.getProperty();

        document.put(type + "Name", property.getName());
        document.put(type + "Value", property.getValue());
        document.put(type + "Signature", property.getSignature());
    }

    public static DisguisePlayerSkin fromJson(Document document, String type) {
        return new DisguisePlayerSkin(
                UUID.fromString(document.getString(type + "ProfileUuid")),
                new Property(document.getString(type + "Name"), document.getString(type + "Value"),
                        document.getString(type + "Signature")));
    }

    public static void toJson(JsonObject data, String type, DisguisePlayerSkin skin) {
        data.addProperty(type + "ProfileUuid", skin.getProfileUuid().toString());

        Property property = skin.getProperty();

        data.addProperty(type + "Name", property.getName());
        data.addProperty(type + "Value", property.getValue());
        data.addProperty(type + "Signature", property.getSignature());
    }

    public static DisguisePlayerSkin fromJson(JsonObject object, String type) {
        return new DisguisePlayerSkin(
                UUID.fromString(object.get(type + "ProfileUuid").getAsString()),
                new Property(object.get(type + "Name").getAsString(), object.get(type + "Value").getAsString(),
                        object.get(type + "Signature").getAsString()));
    }
}
