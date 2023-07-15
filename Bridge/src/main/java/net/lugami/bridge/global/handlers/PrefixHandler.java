package net.lugami.bridge.global.handlers;

import com.google.common.collect.Lists;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.prefix.Prefix;
import org.bson.Document;

import java.util.List;

public class PrefixHandler {

    private List<Prefix> prefixes = Lists.newArrayList();

    private final MongoHandler mongoHandler = BridgeGlobal.getMongoHandler();

    public void init(){
        int index = 0;
        for(Document document : mongoHandler.getPrefixesCollection().find()){
            prefixes.add(parsePrefix(document));
            index++;
        }
//        BridgeGlobal.sendLog(Chat.LIGHT_GREEN + "Loaded a total of " + Chat.YELLOW + index + " prefix" + (index == 1 ? "" : "s") + Chat.LIGHT_GREEN + ".");
    }

    public void recalculatePrefixes(){
        List<Prefix> temp = Lists.newArrayList();
        for(Document document : mongoHandler.getPrefixesCollection().find()){
            temp.add(parsePrefix(document));
        }
        this.prefixes = temp;
    }

    public List<Prefix> getPrefixesInOrder(){
        List<Prefix> prefixes = Lists.newArrayList(this.prefixes);
        prefixes.sort((o1, o2) -> o2.getPriority() - o1.getPriority());
        return prefixes;
    }

    public Prefix getPrefix(String id){
        for(Prefix prefix : prefixes){
            if(prefix.getId().equalsIgnoreCase(id.toLowerCase())){
                return prefix;
            }
        }
        return null;
    }

    public boolean prefixExists(String id){
        for(Prefix prefix : prefixes){
            if(prefix.getId().equals(id.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public void createPrefix(String name){
        mongoHandler.getPrefixesCollection().insertOne(parsePrefixToDocument(new Prefix(name.toLowerCase(), name, "&7[&f" + name + "&7]&f", "&f", 1)));
    }

    public void deletePrefix(Prefix prefix){
        mongoHandler.getPrefixesCollection().deleteOne(parsePrefixToDocument(prefix));
    }

    public Prefix parsePrefix(Document document){
        return new Prefix(document.getString("id"), document.getString("displayName"), document.getString("prefix"), document.getString("color"), document.getInteger("priority"));
    }

    public Document parsePrefixToDocument(Prefix prefix){
        return new Document("id", prefix.getId())
                .append("displayName", prefix.getDisplayName())
                .append("prefix", prefix.getPrefix())
                .append("color", prefix.getColor())
                .append("priority", prefix.getPriority());
    }

}