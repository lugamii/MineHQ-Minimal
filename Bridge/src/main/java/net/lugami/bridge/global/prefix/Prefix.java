package net.lugami.bridge.global.prefix;

import net.lugami.bridge.BridgeGlobal;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;

@Data
@AllArgsConstructor
public class Prefix {

    private String id;
    private String displayName;
    private String prefix;
    private String color;
    private int priority;

    public void save(){
        Document filter = new Document("id", this.id);
        Document old = BridgeGlobal.getMongoHandler().getPrefixesCollection().find(filter).first();
        if(old == null) return;
        Document replace = new Document("id", this.id)
                .append("displayName", this.displayName)
                .append("prefix", this.prefix)
                .append("color", this.color)
                .append("priority", this.priority);
        BridgeGlobal.getMongoHandler().getPrefixesCollection().replaceOne(old, replace);
    }

}