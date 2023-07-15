package net.lugami.bridge.global.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import net.lugami.bridge.global.punishment.Punishment;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.grant.Grant;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.ranks.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lugami.bridge.global.filter.Filter;
import net.lugami.bridge.global.filter.FilterAction;
import net.lugami.bridge.global.filter.FilterType;
import org.bson.Document;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MongoHandler {


    @Getter
    private final MongoClient mongoClient;
    @Getter
    private MongoDatabase database;
    @Getter
    private MongoCollection<Document> rankCollection;
    @Getter
    private MongoCollection<Document> profileCollection;
    @Getter
    private MongoCollection<Document> punishmentCollection;
    @Getter
    private MongoCollection<Document> filterCollection;
    @Getter
    private MongoCollection<Document> disguiseSkinCollection;
    @Getter
    private MongoCollection<Document> prefixesCollection;


    public MongoHandler() {
        String userPart = "";
        if (BridgeGlobal.isMongoAuth()) {
            userPart = BridgeGlobal.getMongoUsername() + ":" + BridgeGlobal.getMongoPassword() + "@";
        }
        mongoClient = new MongoClient(new MongoClientURI("mongodb://" + userPart + BridgeGlobal.getMongoHost() + ":" + BridgeGlobal.getMongoPort() + (BridgeGlobal.isMongoAuth() ? "/admin" : "")));

        try {
            database = mongoClient.getDatabase(BridgeGlobal.getMongoDatabase());
            rankCollection = database.getCollection("ranks");
            profileCollection = database.getCollection("profiles");
            punishmentCollection = database.getCollection("punishments");
            filterCollection = database.getCollection("filters");
            disguiseSkinCollection = database.getCollection("disguiseSkins");
            prefixesCollection = database.getCollection("prefixes");
        } catch (Exception ex) {
            BridgeGlobal.sendLog("§cFailed to initialize backend.");
            BridgeGlobal.sendLog(ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }

    /*/
        Rank - MongoDB
    /*/

    public void getRanksInDB(Consumer<Set<UUID>> callback) {
        Set<UUID> ranks = new HashSet<>();
        for (Document document : rankCollection.find()) {
            ranks.add(UUID.fromString(document.get("uuid").toString()));
        }
        callback.accept(ranks);
    }

    public Document getPunishmentByUUID(UUID uuid) {
        return this.punishmentCollection.find(Filters.eq("uuid", uuid.toString())).first();
    }

    public void loadRank(UUID id, Consumer<Rank> callback, boolean async) {
        if (async) {
            new Thread(() -> loadRank(id, callback, false)).start();
            return;
        }
        Rank rank;
        if (BridgeGlobal.getRankHandler().getRankByID(id) != null) {
            rank = BridgeGlobal.getRankHandler().getRankByID(id);
        } else {
            rank = new Rank(id, false);
        }

        Document document = rankCollection.find(Filters.eq("uuid", id.toString())).first();

        if (document == null) {
            callback.accept(null);
            return;
        }
        rank.setName(document.getString("name"));
        rank.setDisplayName(document.getString("displayName"));
        rank.setPriority(document.getInteger("priority"));
        rank.setStaff(document.getBoolean("staff"));
        rank.setMedia(document.getBoolean("media"));
        rank.setHidden(document.getBoolean("hidden"));
        rank.setGrantable(document.getBoolean("grantable"));
        rank.setDefaultRank(document.getBoolean("defaultRank"));
        rank.setColor(document.getString("color"));
        rank.setPrefix(document.getString("prefix"));
        rank.setSuffix(document.getString("suffix"));

        /*/ old perms /*/
        if (document.get("permissions") != null) {
            List<String> oldPerms = document.getList("permissions", String.class);
            oldPerms.forEach(s -> rank.getPermissions().put(s, "Global"));
        }

        if (document.get("permission") != null) {
            rank.getPermissions().putAll(new Gson().fromJson(document.getString("permission"), new TypeToken<Map<String, String>>() {
            }.getType()));
        }
        if (document.get("metadata") != null) {
            rank.getMetaData().putAll(new Gson().fromJson(document.getString("metadata"), new TypeToken<Map<String, String>>() {
            }.getType()));
        }

        ArrayList<UUID> inherits = new ArrayList<>();
        document.getList("inheritance", String.class).forEach(rankID -> inherits.add(UUID.fromString(rankID)));
        rank.setInherits(inherits);
        callback.accept(rank);
    }

    public void saveRank(Rank rank, Consumer<Boolean> callback, boolean async) {
        if (async) {
            new Thread(() -> saveRank(rank, callback, false)).start();
            return;
        }

        Document document = new Document()
                .append("uuid", rank.getUuid().toString())
                .append("name", rank.getName())
                .append("displayName", rank.getDisplayName())
                .append("priority", rank.getPriority())
                .append("staff", rank.isStaff())
                .append("media", rank.isMedia())
                .append("builder", rank.isBuilder())
                .append("hidden", rank.isHidden())
                .append("grantable", rank.isGrantable())
                .append("defaultRank", rank.isDefaultRank())
                .append("color", rank.getColor())
                .append("prefix", rank.getPrefix())
                .append("suffix", rank.getSuffix())
                .append("permission", new Gson().toJson(rank.getPermissions()))
                .append("metadata", new Gson().toJson(rank.getMetaData()))
                .append("inheritance", rank.getInherits().stream().map(rankIn -> rankIn.getUuid().toString()).collect(Collectors.toList()));

        rankCollection.replaceOne(Filters.eq("uuid", rank.getUuid().toString()), document, new ReplaceOptions().upsert(true));
        callback.accept(true);
    }

    public void removeRank(UUID id, Consumer<Boolean> callback, boolean async) {
        if (async) {
            new Thread(() -> removeRank(id, callback, false)).start();
            return;
        }
        Rank rank = BridgeGlobal.getRankHandler().getRankByID(id);
        if (rank == null) {
            callback.accept(false);
            return;
        }

        rankCollection.deleteOne(Filters.eq("uuid", rank.getUuid().toString()));
        BridgeGlobal.getRankHandler().getRanks().remove(rank);
        callback.accept(true);
    }

    public void getRankInherits(Rank rank, Consumer<List<UUID>> callback, boolean async) {
        if (async) {
            new Thread(() -> getRankInherits(rank, callback, false)).start();
            return;
        }
        Document document = rankCollection.find(Filters.eq("uuid", rank.getUuid().toString())).first();

        if (document == null) {
            callback.accept(null);
            return;
        }
        List<UUID> inherits = new ArrayList<>();

        document.getList("inheritance", String.class).forEach(id -> inherits.add(UUID.fromString(id)));
        callback.accept(inherits);
    }

    /*/
        Prefixes - MongoDB
    /*/

//    public void savePrefix(){
//        Document filter = new Document("id", this.id);
//        Document old = BridgeGlobal.getMongoHandler().getPrefixes().find(filter).first();
//        if(old == null) return;
//        Document replace = new Document("id", this.id)
//                .append("displayName", this.displayName)
//                .append("prefix", this.prefix)
//                .append("color", this.color)
//                .append("priority", this.priority);
//        BridgeGlobal.getMongoHandler().getPrefixes().replaceOne(old, replace);
//    }


    /*/
        Profile - MongoDB
    /*/

    public void getProfilesInDB(Consumer<Set<UUID>> callback) {
        Set<UUID> profiles = new HashSet<>();
        for (Document document : profileCollection.find()) {
            profiles.add(UUID.fromString(document.get("uuid").toString()));
        }
        callback.accept(profiles);
    }

    public void loadProfile(String input, Consumer<Profile> callback, boolean async, LoadType loadType) {
        if (async) {
            new Thread(() -> loadProfile(input, callback, false, loadType)).start();
            return;
        }
        if (loadType == LoadType.UUID) {
            try {
                UUID uuid = UUID.fromString(input);
            } catch (Exception e) {
                callback.accept(null);
                return;
            }
        }

        Document document = profileCollection.find(Filters.regex(loadType.getObjectName(), Pattern.compile("^" + input + "$", Pattern.CASE_INSENSITIVE))).first();
        if (document == null) {
            callback.accept(null);
            return;
        }

        Profile profile = new Profile(document.getString("name"), UUID.fromString(document.getString("uuid")), false);
        if (document.get("permission") != null) {
            profile.getPermissions().putAll(new Gson().fromJson(document.getString("permission"), new TypeToken<Map<String, String>>() {
            }.getType()));
        }
        List<Grant> grants = new ArrayList<>();

        document.getList("grants", String.class).forEach(grantStr -> grants.add(Grant.deserialize(grantStr)));

        profile.getGrants().addAll(grants);
        if (document.containsKey("prefix")) profile.setPrefix(document.getString("prefix"));
        if (document.containsKey("suffix")) profile.setSuffix(document.getString("suffix"));
        if (document.containsKey("color")) profile.setColor(document.getString("color"));

        if (document.containsKey("currentIP")) profile.setCurrentIPAddress(document.getString("currentIP"));
        if (document.containsKey("previousIPs"))
            profile.setPreviousIPAddresses(new Gson().fromJson(document.getString("previousIPs"), new TypeToken<HashSet<String>>() {
            }.getType()));
        if (document.containsKey("bypassPunishmentAccounts"))
            profile.setBypassPunishmentAccounts(new Gson().fromJson(document.getString("bypassPunishmentAccounts"), new TypeToken<HashSet<UUID>>() {
            }.getType()));
        if (document.containsKey("punishments"))
            profile.setPunishments(new Gson().fromJson(document.getString("punishments"), new TypeToken<HashSet<Punishment>>() {
            }.getType()));

        if (document.containsKey("staffPunishments"))
            profile.setStaffPunishments(new Gson().fromJson(document.getString("staffPunishments"), new TypeToken<HashSet<Punishment>>() {
            }.getType()));

        if (document.get("firstJoined") instanceof Integer) {
            profile.setFirstJoined(System.currentTimeMillis());
        } else {
            profile.setFirstJoined(document.getLong("firstJoined"));
        }

        if (document.get("lastJoined") != null) profile.setLastJoined(document.getLong("lastJoined"));
        if (document.get("lastQuit") != null) profile.setLastQuit(document.getLong("lastQuit"));
        if (document.get("connectedServer") != null) profile.setConnectedServer(document.getString("connectedServer"));
        if (document.get("metadata") != null) {
            profile.getMetaData().putAll(new Gson().fromJson(document.getString("metadata"), new TypeToken<Map<String, String>>() {
            }.getType()));
        }
        if(document.get("secretKey") != null) profile.setSecretKey(document.getString("secretKey"));
        if(document.get("becameStaffOn") != null)  profile.setBecameStaffOn(document.getLong("becameStaffOn"));
        if(document.get("removedStaffOn") != null) profile.setRemovedStaffOn(document.getLong("removedStaffOn"));
        callback.accept(profile);
    }


    public void saveProfile(Profile profile, Consumer<Boolean> callback, boolean async) {
        if (async) {
            new Thread(() -> saveProfile(profile, callback, false)).start();
            return;
        }
        List<String> allGrants = new ArrayList<>();
        profile.getGrants().forEach(grant -> allGrants.add(Grant.serialize(grant).toString()));
        Document document = new Document()
                .append("uuid", profile.getUuid().toString())
                .append("name", profile.getUsername())
                .append("prefix", profile.getPrefix())
                .append("suffix", profile.getSuffix())
                .append("color", profile.getColor())
                .append("permission", new Gson().toJson(profile.getPermissions()))
                .append("grants", allGrants)

                .append("currentIP", profile.getCurrentIPAddress())
                .append("previousIPs", new Gson().toJson(profile.getPreviousIPAddresses()))
                .append("bypassPunishmentAccounts", new Gson().toJson(profile.getBypassPunishmentAccounts()))
                .append("punishments", new Gson().toJson(profile.getPunishments()))
                .append("staffPunishments", new Gson().toJson(profile.getStaffPunishments()))

                .append("firstJoined", profile.getFirstJoined())
                .append("lastJoined", profile.getLastJoined())
                .append("lastQuit", profile.getLastQuit())
                .append("connectedServer", profile.getConnectedServer())
                .append("metadata", new Gson().toJson(profile.getMetaData()))
                .append("secretKey", profile.getSecretKey().isEmpty() ? "" : profile.getSecretKey())
                .append("becameStaffOn", profile.getBecameStaffOn())
                .append("removedStaffOn", profile.getRemovedStaffOn());

        profileCollection.replaceOne(Filters.eq("uuid", profile.getUuid().toString()), document, new ReplaceOptions().upsert(true));
        callback.accept(true);
    }

    public void removeProfile(UUID uuid, Consumer<Boolean> callback, boolean async) {
        if (async) {
            new Thread(() -> removeProfile(uuid, callback, false)).start();
            return;
        }
        profileCollection.deleteOne(Filters.eq("uuid", uuid.toString()));
        callback.accept(true);
    }

    @SuppressWarnings("This should never be ran on the main-thread!")
    public List<Profile> getProfiles() {
        List<Profile> profiles = new ArrayList<>();
        for (Document document : BridgeGlobal.getMongoHandler().getProfileCollection().find()) {
            BridgeGlobal.getMongoHandler().loadProfile(document.getString("name"), profiles::add, false, LoadType.USERNAME);
        }
        return profiles;
    }

    @SuppressWarnings("This should never be ran on the main-thread!")
    public List<Profile> getProfiles(String ip) {
        List<Profile> profiles = new ArrayList<>();
        BridgeGlobal.getMongoHandler().getProfiles(ip, profiles::addAll, false);

        return profiles;
    }

    public void getProfiles(String ip, Consumer<List<Profile>> callback, boolean async) {
        if (async) {
            new Thread(() -> getProfiles(ip, callback, false)).start();
            return;
        }
        if (ip == null || ip.equals("N/A")) {
            callback.accept(null);
            return;
        }
        boolean shouldGetAll = ip.toLowerCase().equals("*");
        FindIterable<Document> document = (shouldGetAll ? profileCollection.find() : profileCollection.find(Filters.eq("currentIP", ip)));
        List<Profile> profiles = new ArrayList<>();
        for (Document doc : document) {
            if (doc == null) {
                continue;
            }
            profiles.add(BridgeGlobal.getProfileHandler().getProfileByUUIDOrCreate(UUID.fromString(doc.getString("uuid"))));
        }
        callback.accept(profiles);
    }

    @AllArgsConstructor
    @Getter
    public enum LoadType {

        USERNAME("name"),
        IP("currentIP"),
        UUID("uuid");

        private final String objectName;

    }

    /*
        Filters - MongoDB
     */

    public void getFiltersInDB(Consumer<ArrayList<UUID>> callback) {
        ArrayList<UUID> filters = new ArrayList<>();
        for (Document document : filterCollection.find()) {
            filters.add(UUID.fromString(document.get("uuid").toString()));
        }
        callback.accept(filters);
    }

    public void loadFilter(UUID uuid, Consumer<Filter> callback, boolean async) {
        if (async) {
            new Thread(() -> loadFilter(uuid, callback, false)).start();
            return;
        }

        Document document = filterCollection.find(Filters.regex("uuid", uuid.toString())).first();
        if (document == null) {
            callback.accept(null);
            return;
        }

        callback.accept(new Filter(uuid,
                FilterType.valueOf(document.getString("filterType")),
                FilterAction.valueOf(document.getString("filterAction")),
                document.getString("pattern"),
                document.getLong("muteTime")));
    }


    public void saveFilter(Filter filter, Consumer<Boolean> callback, boolean async) {
        if (async) {
            new Thread(() -> saveFilter(filter, callback, false)).start();
            return;
        }
        Document document = new Document()
                .append("uuid", filter.getUuid().toString())
                .append("filterType", filter.getFilterType().name())
                .append("filterAction", filter.getFilterAction().name())
                .append("pattern", filter.getPattern())
                .append("muteTime", filter.getMuteTime());

        filterCollection.replaceOne(Filters.eq("uuid", filter.getUuid().toString()), document, new ReplaceOptions().upsert(true));
        callback.accept(true);
    }

    public void removeFilter(UUID uuid, Consumer<Boolean> callback, boolean async) {
        if (async) {
            new Thread(() -> removeFilter(uuid, callback, false)).start();
            return;
        }
        filterCollection.deleteOne(Filters.eq("uuid", uuid.toString()));
        callback.accept(true);
    }
}