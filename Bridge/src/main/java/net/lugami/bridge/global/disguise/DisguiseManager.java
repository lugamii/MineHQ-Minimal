package net.lugami.bridge.global.disguise;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import net.lugami.bridge.global.disguise.runnable.DisguiseRunnable;
import net.lugami.bridge.global.packet.PacketHandler;
import net.lugami.bridge.global.packet.types.DisguisePacket;
import net.lugami.bridge.global.profile.Profile;
import net.lugami.bridge.global.ranks.Rank;
import net.lugami.bridge.global.util.Tasks;
import net.lugami.bridge.global.util.mojang.GameProfileUtil;
import net.lugami.bridge.global.util.mojang.UUIDFetcher;
import net.lugami.qlib.nametag.FrozenNametagHandler;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.global.util.Msg;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DisguiseManager {

	private static final DisguisePlayer DEFAULT_PLAYER = new DisguisePlayer(null);

	@Getter
	private final Map<UUID, DisguisePlayer> disguisePlayers;

	@Getter
	private final Map<String, DisguiseProfile> disguiseProfiles;

	public DisguiseManager(boolean bungee) {
		this.disguisePlayers = Maps.newConcurrentMap();
		this.disguiseProfiles = Maps.newHashMap();

		if(!bungee) {
			Tasks.runAsync(this::loadProfiles);
		}
	}

	public DisguiseProfile getRandomDisguiseProfile() {
		List<DisguiseProfile> profiles = new ArrayList<>(this.disguiseProfiles.values());
		return profiles.get(ThreadLocalRandom.current().nextInt(profiles.size()));
	}

	public DisguiseProfile getProfile(String name) {
		DisguiseProfile profile = this.disguiseProfiles.get(name);
		return profile != null ? profile : this.disguiseProfiles.values().stream()
				.filter(p -> p.getName().equalsIgnoreCase(name))
				.findFirst().orElse(null);
	}

	public boolean addProfile(String name, String skin) {
		DisguiseProfile profile = new DisguiseProfile(name);
		profile.setSkinName(skin);

		DisguisePlayerSkin disguisePlayerSkin = this.loadProfileSkin(name, skin);

		if(disguisePlayerSkin != null) {
			profile.setSkin(disguisePlayerSkin);

			this.disguiseProfiles.put(name, profile);

			Tasks.runAsync(() -> this.saveProfiles(false));
			return true;
		}

		return false;
	}

	public boolean removeProfile(String name) {
		DisguiseProfile profile = this.disguiseProfiles.remove(name);

		Tasks.runAsync(() -> this.saveProfiles(false));

		return profile != null;
	}

	private void loadProfiles() {
		for (Document document : BridgeGlobal.getMongoHandler().getDisguiseSkinCollection().find()) {
			DisguiseProfile profile = new DisguiseProfile(document.getString("name"));
			profile.setSkinName(document.getString("nameSkin"));
			profile.setDisplayName(document.getString("displayName"));

			try {
				profile.setSkin(DisguisePlayerSkin.fromJson(document, "skin"));
			} catch (Exception e) {
				Msg.logConsole("[Disguise] Skin of " + profile.getName() + " didn't load properly! Trying to load with mojang api.");
				Msg.logConsole(document.toString());

				DisguisePlayerSkin skin = this.loadProfileSkin(profile.getName(), profile.getSkinName());

				if(skin == null) {
					continue;
				}

				profile.setSkin(skin);
			}

			if(profile.getName() != null && profile.getSkinName() != null && profile.getSkin() != null) {
				this.disguiseProfiles.put(profile.getName(), profile);
			}
		}
	}

	private DisguisePlayerSkin loadProfileSkin(String name, String skinName) {
		GameProfile gameProfile = GameProfileUtil.getSkinCache().get(skinName);

		if(gameProfile == null) {
			Map<String, UUID> fetched;

			try {
				fetched = new UUIDFetcher(Collections.singletonList(skinName)).call();
			} catch (Exception ignored) {
				Msg.logConsole("[Disguise] Failed to fetch skin properties of " + skinName + '!');
				return null;
			}

			Optional<UUID> fetchedUuid = fetched.values().stream().findFirst();

			if(!fetchedUuid.isPresent()) {
				Msg.logConsole("[Disguise] Failed to load skin with Mojang API of " + name + "!");
				return null;
			}

			gameProfile = GameProfileUtil.loadGameProfile(fetchedUuid.get(), skinName);
		}

		DisguisePlayerSkin skin = this.setupDisguiseSkin(gameProfile);

		if(skin == null) {
			Msg.logConsole("[Disguise] The skin was found and loaded with Mojang API but something went wrong while setting it up.");
			return null;
		}

		return skin;
	}

	public void saveProfiles(boolean async) {
		for(DisguiseProfile profile : this.disguiseProfiles.values()) {
			Document document = new Document();

			document.put("name", profile.getName());
			document.put("nameSkin", profile.getSkinName());
			document.put("displayName", profile.getDisplayName());

			DisguisePlayerSkin.toJson(document, "skin", profile.getSkin());

			BridgeGlobal.getMongoHandler().getDisguiseSkinCollection().replaceOne(Filters.eq("name", profile.getName()), document, new ReplaceOptions().upsert(true));
		}
	}

	public void load(String name, UUID uuid) {
		JsonObject object = BridgeGlobal.getPlayerData(uuid, "disguise");

		if(object != null) {

			DisguisePlayer player = new DisguisePlayer(name);
			player.setDisguiseRank(object.get("disguiseRank") != null ? BridgeGlobal.getRankHandler().getRankByName(object.get("disguiseRank").getAsString()) : BridgeGlobal.getRankHandler().getDefaultRank());
			player.setDisguiseName(object.get("disguiseName").getAsString());
			player.setDisguiseSkin(object.get("disguiseSkin").getAsString());

			try {
				player.setRealSkin(DisguisePlayerSkin.fromJson(object, "real"));
				player.setFakeSkin(DisguisePlayerSkin.fromJson(object, "fake"));
			} catch (Exception e) {
				Msg.logConsole("[Disguise] Deleted data of " + name + " because it didn't load properly!");
				BridgeGlobal.deletePlayerData("disguise", uuid);
				e.printStackTrace();
				return;
			}

			this.disguisePlayers.put(uuid, player);
		}
	}

	public void save(UUID uuid) {
		this.save(uuid, false);
	}

	public void save(UUID uuid, boolean async) {
		DisguisePlayer player = this.disguisePlayers.get(uuid);

		if(player != null) {
			JsonObject object = new JsonObject();
			JsonObject data = new JsonObject();

			data.addProperty("disguiseRank", player.getDisguiseRank().getName());
			data.addProperty("disguiseName", player.getDisguiseName());
			data.addProperty("disguiseSkin", player.getDisguiseSkin());

			DisguisePlayerSkin.toJson(data, "real", player.getRealSkin());
			DisguisePlayerSkin.toJson(data, "fake", player.getFakeSkin());

			object.add("data", data);

			Tasks.run(() -> BridgeGlobal.savePlayerData("disguise", uuid, object), async);
		}
	}

	public void deleteData(UUID uuid) {
		this.deleteData(uuid, false);
	}

	public void deleteData(UUID uuid, boolean async) {
		this.disguisePlayers.remove(uuid);
		Tasks.run(() -> BridgeGlobal.deletePlayerData("disguise", uuid), async);
	}

	public void leave(UUID uuid) {
		this.disguisePlayers.remove(uuid);
	}

	public DisguisePlayer getAndCreatePlayer(UUID uuid, String name) {
		this.disguisePlayers.putIfAbsent(uuid, new DisguisePlayer(name));
		return this.disguisePlayers.get(uuid);
	}

	public DisguisePlayer getPlayer(UUID uuid) {
		return this.disguisePlayers.getOrDefault(uuid, DEFAULT_PLAYER);
	}

	public boolean disguise(Player player, DisguisePlayer disguisePlayer, String profileName, boolean join, boolean full, boolean checkName) throws Exception {
		String disguiseName = disguisePlayer.getDisguiseName();

		if (checkName && Bukkit.getPlayer(disguiseName) != null) {
			player.sendMessage(ChatColor.RED + "Failed to disguise you because the player that you were assigned as is already online.");
			this.deleteData(player.getUniqueId());
			return false;
		}

		GameProfile profile;
		if (join) {
			DisguisePlayerSkin fakeSkin = disguisePlayer.getFakeSkin();

			profile = new GameProfile(fakeSkin.getProfileUuid(), disguisePlayer.getDisguiseName());
			profile.getProperties().put("textures", fakeSkin.getProperty());
		} else {
			DisguisePlayerSkin realSkin = this.setupDisguiseSkin(((CraftPlayer) player).getProfile());

			if (realSkin == null) {
				player.sendMessage(ChatColor.RED + "Failed to disguise you because the properties of real skin are missing! Contact developer.");
				return false;
			}

			DisguisePlayer dpOld = this.disguisePlayers.get(player.getUniqueId());
			disguisePlayer.setRealSkin(dpOld != null ? dpOld.getRealSkin() : realSkin);

			String skin = disguisePlayer.getDisguiseSkin();
			DisguisePlayerSkin fakeSkin;

			DisguiseProfile disguiseProfile = this.disguiseProfiles.values()
					.stream()
					.filter(dp -> dp.getName().equalsIgnoreCase(profileName != null ? profileName : skin))
					.findFirst()
					.orElse(null);

			// if disguise profile is made get it from cache, if not load it or get it from other cache
			if (disguiseProfile != null) {
				fakeSkin = disguiseProfile.getSkin();

//				Msg.logConsole("");
//				Msg.logConsole("Found fake skin:");
//				Msg.logConsole(" - Name: " + disguiseProfile.getName());
//				Msg.logConsole(" - Skin Name: " + disguiseProfile.getSkinName());
//				Msg.logConsole(" - Skin UUID: " + fakeSkin.getProfileUuid().toString());
//				Msg.logConsole(" - Value: " + fakeSkin.getProperty().getValue());
//				Msg.logConsole(" - Signature: " + fakeSkin.getProperty().getSignature());

				profile = new GameProfile(fakeSkin.getProfileUuid(), disguisePlayer.getDisguiseName());
				profile.getProperties().put("textures", fakeSkin.getProperty());
			} else {
				profile = GameProfileUtil.getSkinCache().get(skin.toLowerCase());

				Msg.logConsole("Loading skin or getting from cache -> " + skin);

				if (profile == null) {
					Map<String, UUID> fetched = new UUIDFetcher(Collections.singletonList(skin)).call();

					Optional<UUID> fetchedUuid = fetched.values().stream().findFirst();
					if (fetchedUuid.isPresent()) {
						profile = GameProfileUtil.loadGameProfile(fetchedUuid.get(), skin);
					} else {
						profile = GameProfileUtil.loadGameProfile(UUID.fromString("8667ba71-b85a-4004-af54-457a9734eed7"), "Steve");
					}
				}

				fakeSkin = this.setupDisguiseSkin(profile);
			}

			if (fakeSkin == null) {
				player.sendMessage(ChatColor.RED + "Failed to disguise you because the properties of fake skin are missing! Contact developer.");
				return false;
			}

			disguisePlayer.setFakeSkin(fakeSkin);

			this.disguisePlayers.put(player.getUniqueId(), disguisePlayer);
		}

		if(full) {
			GameProfile finalProfile = profile;
			Tasks.run(() -> {
				new DisguiseRunnable(player, finalProfile, disguiseName).run();

				Profile playerProfile = BridgeGlobal.getProfileHandler().getProfileByUUID(player.getUniqueId());
				player.setPlayerListName(disguisePlayer.getDisguiseRank().getColor() + disguiseName);
				player.setDisplayName(disguisePlayer.getDisguiseRank().getColor() + disguiseName);
				player.setCustomName(disguisePlayer.getDisguiseRank().getColor() + disguiseName);
				playerProfile.updateColor();
				playerProfile.saveProfile();
				PacketHandler.sendToAll(new DisguisePacket("&9[Staff] &7[" + playerProfile.getConnectedServer() + "] " + playerProfile.getCurrentGrant().getRank().getColor() + playerProfile.getUsername() + " &bdisguised as " + disguisePlayer.getDisguiseRank().getColor() + disguiseName));
			});
		} else {
			Profile playerProfile = BridgeGlobal.getProfileHandler().getProfileByUUID(player.getUniqueId());
			player.setPlayerListName(disguisePlayer.getDisguiseRank().getColor() + disguiseName);
			player.setDisplayName(disguisePlayer.getDisguiseRank().getColor() + disguiseName);
			player.setCustomName(disguisePlayer.getDisguiseRank().getColor() + disguiseName);
			playerProfile.updateColor();
			playerProfile.saveProfile();
		}

		if(!join) {
			this.save(player.getUniqueId());
		}

		Tasks.run(() -> {
			FrozenNametagHandler.reloadPlayer(player);
			FrozenNametagHandler.reloadOthersFor(player);
		});

		return true;
	}

	public void undisguise(Player player, boolean full, boolean quit) {
		DisguisePlayer disguisePlayer = this.disguisePlayers.get(player.getUniqueId());

		if(disguisePlayer != null) {
			DisguisePlayerSkin realSkin = disguisePlayer.getRealSkin();
			Profile profile = BridgeGlobal.getProfileHandler().getProfileByUUID(player.getUniqueId());

			GameProfile originalProfile = new GameProfile(realSkin.getProfileUuid(), profile.getUsername());
			originalProfile.getProperties().put("textures", realSkin.getProperty());

			if (full) {
				Tasks.run(new DisguiseRunnable(player, originalProfile, profile.getUsername()));
			}

			this.deleteData(player.getUniqueId());

			Rank rank = profile.getCurrentGrant().getRank();
			profile.updateColor();
			player.setPlayerListName(rank.getColor() + profile.getUsername());
			player.setDisplayName(rank.getColor() + profile.getUsername());
			player.setCustomName(rank.getColor() + profile.getUsername());
			profile.saveProfile();
			PacketHandler.sendToAll(new DisguisePacket("&9[Staff] &7[" + profile.getConnectedServer() + "] " + profile.getCurrentGrant().getRank().getColor() + profile.getUsername() + " &bundisguised"));

			if(!quit) {
				MinecraftServer.getServer().getPlayerList().playerMap.put(profile.getUsername(), ((CraftPlayer) player).getHandle());
//				Bukkit.broadcastMessage("Putting " + profile.getUsername());
			}

			MinecraftServer.getServer().getPlayerList().playerMap.remove(player.getName());

//			Bukkit.broadcastMessage("Removing " + profile.getUsername());

			Tasks.run(() -> {
				FrozenNametagHandler.reloadPlayer(player);
				FrozenNametagHandler.reloadOthersFor(player);
			});
		}
	}

	public DisguisePlayerSkin setupDisguiseSkin(GameProfile profile) {
		DisguisePlayerSkin skin = new DisguisePlayerSkin();
		skin.setProfileUuid(profile.getId());

		Collection<Property> properties = profile.getProperties().get("textures");

		if(properties == null || properties.isEmpty()) {
			return null;
		}

		for(Property property : properties) {
			skin.setProperty(property);
		}

		return skin;
	}
}