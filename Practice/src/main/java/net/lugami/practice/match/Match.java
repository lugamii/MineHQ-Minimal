package net.lugami.practice.match;

import com.cheatbreaker.api.CheatBreakerAPI;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.google.gson.JsonObject;
import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import lombok.Setter;
import net.lugami.practice.kittype.KitType;
import net.lugami.practice.match.event.*;
import net.lugami.practice.match.replay.ReplayableAction;
import net.lugami.practice.postmatchinv.PostMatchPlayer;
import net.lugami.practice.setting.Setting;
import net.lugami.practice.setting.SettingHandler;
import net.lugami.practice.util.*;
import net.lugami.qlib.nametag.FrozenNametagHandler;
import net.lugami.qlib.util.UUIDUtils;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotConfig;
import net.lugami.practice.Practice;
import net.lugami.practice.arena.Arena;
import net.lugami.practice.elo.EloCalculator;
import net.lugami.practice.lobby.LobbyHandler;
import net.lugami.practice.match.event.*;
import net.lugami.practice.util.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public final class Match {

	private static final int MATCH_END_DELAY_SECONDS = 3;

	@Getter
	private final String _id = UUID.randomUUID().toString().substring(0, 7);

	@Getter
	private final KitType kitType;
	@Getter
	private final Arena arena;
	@Getter
	private final List<MatchTeam> teams; // immutable so @Getter is ok
	private final Map<UUID, PostMatchPlayer> postMatchPlayers = new HashMap<>();
	private final Set<UUID> spectators = new HashSet<>();

	@Getter
	private MatchTeam winner;
	@Getter
	private MatchEndReason endReason;
	@Getter
	private MatchState state;
	@Getter
	private Date startedAt;
	@Getter
	private Date endedAt;
	@Getter
	private boolean ranked;
	@Getter
	private Map<UUID, Integer> wins = new HashMap<>();
	@Getter
	private int matchAmount = 3;
	@Setter
	private boolean threeSecond = false;

	@Getter
	private boolean allowRematches;
	@Getter
	@Setter
	private EloCalculator.Result eloChange;

	// this will keep track of blocks placed by players during this match.
	// it'll only be populated if the KitType allows building in the first place.
	private final Set<BlockVector> placedBlocks = new HashSet<>();

	// we only spectators generate one message (either a join or a leave)
	// per match, to prevent spam. This tracks who has used their one message
	private final transient Set<UUID> spectatorMessagesUsed = new HashSet<>();

	@Getter
	private Map<UUID, Integer> potionsThrown = Maps.newHashMap();
	@Getter
	private Map<UUID, UUID> lastHit = Maps.newHashMap();
	@Getter
	@Setter
	private Location lastHitLocation;
	@Getter
	private Map<UUID, Integer> combos = Maps.newHashMap();
	@Getter
	private Map<UUID, Integer> totalHits = Maps.newHashMap();
	@Getter
	private Map<UUID, Integer> longestCombo = Maps.newHashMap();
	@Getter
	private Map<UUID, Integer> missedPots = Maps.newHashMap();

	@Getter
	private Map<UUID, Integer> thrownPots = Maps.newHashMap();

	@Getter
	private Map<UUID, Integer> thrownDebuffs = Maps.newHashMap();

	@Getter
	private Map<UUID, Integer> missedDebuffs = Maps.newHashMap();

	@Getter
	private List<ReplayableAction> replayableActions = Lists.newArrayList();

	@Getter
	private Set<UUID> allPlayers = Sets.newHashSet();

	@Getter
	private Set<UUID> winningPlayers;
	@Getter
	private Set<UUID> losingPlayers;

	public Match(KitType kitType, Arena arena, List<MatchTeam> teams, boolean ranked, boolean allowRematches) {
		this.kitType = Preconditions.checkNotNull(kitType, "kitType");
		this.arena = Preconditions.checkNotNull(arena, "arena");
		this.teams = ImmutableList.copyOf(teams);
		this.ranked = ranked;
		this.allowRematches = allowRematches;

		saveState();

		if(ranked) {
			for(MatchTeam team : teams) {
				for(UUID uuid : team.getAllMembers()) {
					Player p = Bukkit.getPlayer(uuid);

					CheatBreakerAPI.getInstance().setCompetitiveGame(p, true);
					LunarClientAPI.getInstance().setCompetitiveGame(p, true);
				}
			}
		}
	}

	private void saveState() {
		if (kitType.isBuildingAllowed())
			this.arena.takeSnapshot();
	}

	void startCountdown() {
		state = MatchState.COUNTDOWN;

		Map<UUID, Match> playingCache = Practice.getInstance().getMatchHandler().getPlayingMatchCache();
		Set<Player> updateVisibility = new HashSet<>();

		for (MatchTeam team : this.getTeams()) {
			for (UUID playerUuid : team.getAllMembers()) {

				if (!team.isAlive(playerUuid))
					continue;

				Player player = Bukkit.getPlayer(playerUuid);

				playingCache.put(player.getUniqueId(), this);

				Location spawn = (team == teams.get(0) ? arena.getTeam1Spawn() : arena.getTeam2Spawn()).clone();
				Vector oldDirection = spawn.getDirection();

				Block block = spawn.getBlock();
				while (block.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
					block = block.getRelative(BlockFace.DOWN);
					if (block.getY() <= 0) {
						block = spawn.getBlock();
						break;
					}
				}

				spawn = block.getLocation();
				spawn.setDirection(oldDirection);
				spawn.add(0.5, 0, 0.5);

				player.teleport(spawn);
				player.getInventory().setHeldItemSlot(0);

				FrozenNametagHandler.reloadPlayer(player);
				FrozenNametagHandler.reloadOthersFor(player);

				if (kitType.getKnockbackName() != null) {
					player.setKbProfile(SpigotConfig.getKnockbackByName(kitType.getKnockbackName()));
				}

				playingCache.put(player.getUniqueId(), this);

				updateVisibility.add(player);
				PatchedPlayerUtils.resetInventory(player, GameMode.SURVIVAL);
			}
		}

		// we wait to update visibility until everyone's been put in the player cache
		// then we update vis, otherwise the update code will see 'partial' views of the
		// match
		updateVisibility.forEach(VisibilityUtils::updateVisibilityFlicker);

		Bukkit.getPluginManager().callEvent(new MatchCountdownStartEvent(this));

		new BukkitRunnable() {

			int countdownTimeRemaining = threeSecond ? 3 : 5;

			public void run() {
				if (state != MatchState.COUNTDOWN) {
					cancel();
					return;
				}

				if (countdownTimeRemaining == 0) {
					playSoundAll(Sound.NOTE_PLING, 2F);
					startMatch(threeSecond);
					return; // so we don't send '0...' message
				} else if (countdownTimeRemaining <= 3) {
					playSoundAll(Sound.NOTE_PLING, 1F);
				}

				messageAll(ChatColor.YELLOW.toString() + countdownTimeRemaining + "...");
				countdownTimeRemaining--;
			}

		}.runTaskTimer(Practice.getInstance(), 0L, 20L);
	}
	private void startMatch(boolean sumo) {
		state = MatchState.IN_PROGRESS;
		if (!sumo) {
			startedAt = new Date();
			messageAll(ChatColor.GREEN + "Match started.");
		}
		Bukkit.getPluginManager().callEvent(new MatchStartEvent(this));
	}

	private void startMatch() {
		state = MatchState.IN_PROGRESS;
		startedAt = new Date();

		messageAll(ChatColor.GREEN + "Match started.");
		Bukkit.getPluginManager().callEvent(new MatchStartEvent(this));
	}
	public void resetForSumo() {
		startCountdown();
	}

	public void endMatch(MatchEndReason reason) {
		// prevent duplicate endings
		if (state == MatchState.ENDING || state == MatchState.TERMINATED) {
			return;
		}

		state = MatchState.ENDING;
		endedAt = new Date();
		endReason = reason;

		try {
			for (MatchTeam matchTeam : this.getTeams()) {
				for (UUID playerUuid : matchTeam.getAllMembers()) {
					allPlayers.add(playerUuid);
					if (!matchTeam.isAlive(playerUuid))
						continue;
					Player player = Bukkit.getPlayer(playerUuid);

					if (player!= null) {
						player.setKbProfile(null);
					}

					postMatchPlayers.computeIfAbsent(playerUuid, v -> new PostMatchPlayer(player, kitType, kitType.getHealingMethod(), totalHits.getOrDefault(player.getUniqueId(), 0), longestCombo.getOrDefault(player.getUniqueId(), 0), missedPots.getOrDefault(player.getUniqueId(), 0), thrownPots.getOrDefault(player.getUniqueId(), 0), missedDebuffs.getOrDefault(player.getUniqueId(), 0), thrownDebuffs.getOrDefault(player.getUniqueId(), 0)));
				}
			}

			messageAll(ChatColor.RED + "Match ended.");
			Bukkit.getPluginManager().callEvent(new MatchEndEvent(this));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		int delayTicks = MATCH_END_DELAY_SECONDS * 20;
		if (JavaPlugin.getProvidingPlugin(this.getClass()).isEnabled()) {
			Bukkit.getScheduler().runTaskLater(Practice.getInstance(), this::terminateMatch, delayTicks);
		} else {
			this.terminateMatch();
		}
	}

	private void terminateMatch() {
		// prevent double terminations
		if (state == MatchState.TERMINATED) {
			return;
		}

		state = MatchState.TERMINATED;

		// if the match ends before the countdown ends
		// we have to set this to avoid a NPE in Date#from
		if (startedAt == null) {
			startedAt = new Date();
		}

		// if endedAt wasn't set before (if terminateMatch was called directly)
		// we want to make sure we set an ending time. Otherwise we keep the
		// technically more accurate time set in endMatch
		if (endedAt == null) {
			endedAt = new Date();
		}

		this.winningPlayers = winner.getAllMembers();
		this.losingPlayers = teams.stream().filter(team -> team != winner).flatMap(team -> team.getAllMembers().stream()).collect(Collectors.toSet());

		Bukkit.getPluginManager().callEvent(new MatchTerminateEvent(this));

		// we have to make a few edits to the document so we use Gson (which has
		// adapters
		// for things like Locations) and then edit it
		JsonObject document = Practice.getGson().toJsonTree(this).getAsJsonObject();

		document.addProperty("winner", teams.indexOf(winner)); // replace the full team with their index in the full list
		document.addProperty("arena", arena.getSchematic()); // replace the full arena with its schematic (website doesn't care which copy we
		// used)

		Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), () -> {
			// The Document#parse call really sucks. It generates literally thousands of
			// objects per call.
			// Hopefully we'll be moving to just posting to a web service soon enough (and
			// then we don't have to run
			// Mongo's stupid JSON parser)
			Document parsedDocument = Document.parse(document.toString());
			parsedDocument.put("startedAt", startedAt);
			parsedDocument.put("endedAt", endedAt);
			MongoUtils.getCollection(MatchHandler.MONGO_COLLECTION_NAME).insertOne(parsedDocument);
		});

		MatchHandler matchHandler = Practice.getInstance().getMatchHandler();
		LobbyHandler LobbyHandler = Practice.getInstance().getLobbyHandler();

		Map<UUID, Match> playingCache = matchHandler.getPlayingMatchCache();
		Map<UUID, Match> spectateCache = matchHandler.getSpectatingMatchCache();

		if (kitType.isBuildingAllowed())
			arena.restore();
		Practice.getInstance().getArenaHandler().releaseArena(arena);
		matchHandler.removeMatch(this);

		getTeams().forEach(team -> {
			team.getAllMembers().forEach(player -> {
				if (team.isAlive(player)) {
					playingCache.remove(player);
					spectateCache.remove(player);
					LobbyHandler.returnToLobby(Bukkit.getPlayer(player));
				}
			});
		});


		spectators.forEach(player -> {
			if (Bukkit.getPlayer(player) != null) {
				playingCache.remove(player);
				spectateCache.remove(player);
				LobbyHandler.returnToLobby(Bukkit.getPlayer(player));
			}
		});
	}

	public Set<UUID> getSpectators() {
		return ImmutableSet.copyOf(spectators);
	}

	public Map<UUID, PostMatchPlayer> getPostMatchPlayers() {
		return ImmutableMap.copyOf(postMatchPlayers);
	}

	private void checkEnded() {
		if (state == MatchState.ENDING || state == MatchState.TERMINATED) {
			return;
		}

		List<MatchTeam> teamsAlive = new ArrayList<>();

		for (MatchTeam team : teams) {
			if (!team.getAliveMembers().isEmpty()) {
				teamsAlive.add(team);
			}
		}

		if (teamsAlive.size() == 1) {
			this.winner = teamsAlive.get(0);
			endMatch(MatchEndReason.ENEMIES_ELIMINATED);
		}
	}

	public boolean isSpectator(UUID uuid) {
		return spectators.contains(uuid);
	}

	public void addSpectator(Player player, Player target) {
		addSpectator(player, target, false);
	}

	// fromMatch indicates if they were a player immediately before spectating.
	// we use this for things like teleporting and messages
	public void addSpectator(Player player, Player target, boolean fromMatch) {
		if (!fromMatch && state == MatchState.ENDING) {
			player.sendMessage(ChatColor.RED + "This match is no longer available for spectating.");
			return;
		}

		Map<UUID, Match> spectateCache = Practice.getInstance().getMatchHandler().getSpectatingMatchCache();

		spectateCache.put(player.getUniqueId(), this);
		spectators.add(player.getUniqueId());

		if (!fromMatch) {
			Location tpTo = arena.getSpectatorSpawn();

			if (target != null) {
				// we tp them a bit up so they're not inside of their target
				tpTo = target.getLocation().clone().add(0, 1.5, 0);
			}

			player.teleport(tpTo);
			player.sendMessage(ChatColor.YELLOW + "Now spectating " + ChatColor.AQUA + getSimpleDescription(true) + ChatColor.YELLOW + "...");
			sendSpectatorMessage(player, ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " is now spectating.");
		} else {
			// so players don't accidentally click the item to stop spectating
			player.getInventory().setHeldItemSlot(0);
		}

		FrozenNametagHandler.reloadPlayer(player);
		FrozenNametagHandler.reloadOthersFor(player);

		VisibilityUtils.updateVisibility(player);
		PatchedPlayerUtils.resetInventory(player, GameMode.CREATIVE, true); // because we're about to reset their inv on a timer
		InventoryUtils.resetInventoryDelayed(player);
		player.setAllowFlight(true);
		player.setFlying(true); // called after PlayerUtils reset, make sure they don't fall out of the sky
		ItemListener.addButtonCooldown(player, 1_500);

		Bukkit.getPluginManager().callEvent(new MatchSpectatorJoinEvent(player, this));
	}

	public void removeSpectator(Player player) {
		removeSpectator(player, true);
	}

	public void removeSpectator(Player player, boolean returnToLobby) {
		Map<UUID, Match> spectateCache = Practice.getInstance().getMatchHandler().getSpectatingMatchCache();

		spectateCache.remove(player.getUniqueId());
		spectators.remove(player.getUniqueId());
		ItemListener.addButtonCooldown(player, 1_500);

		sendSpectatorMessage(player, ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " is no longer spectating.");

		if (returnToLobby) {
			Practice.getInstance().getLobbyHandler().returnToLobby(player);
		}

		Bukkit.getPluginManager().callEvent(new MatchSpectatorLeaveEvent(player, this));
	}

	private void sendSpectatorMessage(Player spectator, String message) {
		// see comment on spectatorMessagesUsed field for more
		if (spectator.hasMetadata("ModMode") || !spectatorMessagesUsed.add(spectator.getUniqueId())) {
			return;
		}

		SettingHandler settingHandler = Practice.getInstance().getSettingHandler();

		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online == spectator) {
				continue;
			}

			boolean sameMatch = isSpectator(online.getUniqueId()) || getTeam(online.getUniqueId()) != null;
			boolean spectatorMessagesEnabled = settingHandler.getSetting(online, Setting.SHOW_SPECTATOR_JOIN_MESSAGES);

			if (sameMatch && spectatorMessagesEnabled) {
				online.sendMessage(message);
			}
		}
	}

	public boolean canDie(Player loser) {
		if (!this.getKitType().isSumo()) {
			return true;
		}

		if (this.getState() != MatchState.IN_PROGRESS) {
			return true;
		}

		Player winner = this.getTeams().get(0).getFirstMember() == loser.getUniqueId() ? Bukkit.getPlayer(this.getTeams().get(1).getFirstMember()) : Bukkit.getPlayer(this.getTeams().get(0).getFirstMember());

		AtomicBoolean canDie = new AtomicBoolean(false);

		if (winner == null) {
			return true;
		}

		wins.put(winner.getUniqueId(), wins.getOrDefault(winner.getUniqueId(), 0) + 1);

		if (wins.get(winner.getUniqueId()) >= matchAmount) {
			canDie.set(true);
		}

		if (wins.get(winner.getUniqueId()) < matchAmount) {
			canDie.set(false);
		}

		if (canDie.get()) {
//            messageAll(net.md_5.bungee.api.ChatColor.WHITE + winner.getDisplayName() + net.md_5.bungee.api.ChatColor.YELLOW + " has " + net.md_5.bungee.api.ChatColor.GREEN + "won" + net.md_5.bungee.api.ChatColor.YELLOW + " the match.");
			this.setThreeSecond(false);
		} else {
			winner.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.GREEN + "won" + ChatColor.YELLOW + " the round, you need " + ChatColor.LIGHT_PURPLE + (this.getMatchAmount() - wins.get(winner.getUniqueId())) + ChatColor.YELLOW + " more to win.");
			loser.sendMessage(winner.getDisplayName() + ChatColor.YELLOW + " has " + ChatColor.GREEN + "won" + ChatColor.YELLOW + " the round, they need " + ChatColor.LIGHT_PURPLE + (this.getMatchAmount() - wins.get(winner.getUniqueId())) + ChatColor.YELLOW + " more to win.");
			messageSpectators(winner.getDisplayName() + ChatColor.YELLOW + " has " + ChatColor.GREEN + "won" + ChatColor.YELLOW + " the round, they need " + ChatColor.LIGHT_PURPLE + (this.getMatchAmount() - wins.get(winner.getUniqueId())) + ChatColor.YELLOW + " more to win.");
			this.setThreeSecond(true);
		}

		return canDie.get();
	}

	public void markDead(Player player) {
		MatchTeam team = getTeam(player.getUniqueId());

		if (team == null) {
			return;
		}

		Map<UUID, Match> playingCache = Practice.getInstance().getMatchHandler().getPlayingMatchCache();

		team.markDead(player.getUniqueId());
		playingCache.remove(player.getUniqueId());

		postMatchPlayers.put(player.getUniqueId(), new PostMatchPlayer(player, kitType, kitType.getHealingMethod(), totalHits.getOrDefault(player.getUniqueId(), 0), longestCombo.getOrDefault(player.getUniqueId(), 0), missedPots.getOrDefault(player.getUniqueId(), 0), thrownPots.getOrDefault(player.getUniqueId(), 0), missedDebuffs.getOrDefault(player.getUniqueId(), 0), thrownDebuffs.getOrDefault(player.getUniqueId(), 0)));

		checkEnded();
	}

	public MatchTeam getTeam(UUID playerUuid) {
		for (MatchTeam team : teams) {
			if (team.isAlive(playerUuid)) {
				return team;
			}
		}

		return null;
	}

	public MatchTeam getPreviousTeam(UUID playerUuid) {
		for (MatchTeam team : teams) {
			if (team.getAllMembers().contains(playerUuid)) {
				return team;
			}
		}

		return null;
	}

	/**
	 * Creates a simple, one line description of this match This will include two
	 * players (if a 1v1) or player counts and the kit type
	 *
	 * @return A simple description of this match
	 */
	public String getSimpleDescription(boolean includeRankedUnranked) {
		String players;

		if (teams.size() == 2) {
			MatchTeam teamA = teams.get(0);
			MatchTeam teamB = teams.get(1);

			if (teamA.getAliveMembers().size() == 1 && teamB.getAliveMembers().size() == 1) {
				String nameA = UUIDUtils.name(teamA.getFirstAliveMember());
				String nameB = UUIDUtils.name(teamB.getFirstAliveMember());

				players = nameA + " vs " + nameB;
			} else {
				players = teamA.getAliveMembers().size() + " vs " + teamB.getAliveMembers().size();
			}
		} else {
			int numTotalPlayers = 0;

			for (MatchTeam team : teams) {
				numTotalPlayers += team.getAliveMembers().size();
			}

			players = numTotalPlayers + " player fight";
		}

		if (includeRankedUnranked) {
			String rankedStr = ranked ? "Ranked" : "Unranked";
			return players + " (" + rankedStr + " " + kitType.getDisplayName() + ")";
		} else {
			return players;
		}
	}

	/**
	 * Sends a basic chat message to all alive participants and spectators
	 *
	 * @param message the message to send
	 */
	public void messageAll(String message) {
		messageAlive(message);
		messageSpectators(message);
	}

	/**
	 * Plays a sound for all alive participants and spectators
	 *
	 * @param sound the Sound to play
	 * @param pitch the pitch to play the provided sound at
	 */
	public void playSoundAll(Sound sound, float pitch) {
		playSoundAlive(sound, pitch);
		playSoundSpectators(sound, pitch);
	}

	/**
	 * Sends a basic chat message to all spectators
	 *
	 * @param message the message to send
	 */
	public void messageSpectators(String message) {
		for (UUID spectator : spectators) {
			Player spectatorBukkit = Bukkit.getPlayer(spectator);

			if (spectatorBukkit != null) {
				spectatorBukkit.sendMessage(message);
			}
		}
	}

	/**
	 * Plays a sound for all spectators
	 *
	 * @param sound the Sound to play
	 * @param pitch the pitch to play the provided sound at
	 */
	public void playSoundSpectators(Sound sound, float pitch) {
		for (UUID spectator : spectators) {
			Player spectatorBukkit = Bukkit.getPlayer(spectator);

			if (spectatorBukkit != null) {
				spectatorBukkit.playSound(spectatorBukkit.getEyeLocation(), sound, 10F, pitch);
			}
		}
	}

	/**
	 * Sends a basic chat message to all alive participants
	 *
	 * @param message the message to send
	 * @see MatchTeam#messageAlive(String)
	 */
	public void messageAlive(String message) {
		for (MatchTeam team : teams) {
			team.messageAlive(message);
		}
	}

	/**
	 * Plays a sound for all alive participants
	 *
	 * @param sound the Sound to play
	 * @param pitch the pitch to play the provided sound at
	 */
	public void playSoundAlive(Sound sound, float pitch) {
		for (MatchTeam team : teams) {
			team.playSoundAlive(sound, pitch);
		}
	}

	/**
	 * Records a placed block during this match. Used to keep track of which blocks
	 * can be broken.
	 */
	public void recordPlacedBlock(Block block) {
		placedBlocks.add(block.getLocation().toVector().toBlockVector());
	}

	/**
	 * Checks if a block can be broken in this match. Only used if the KitType
	 * allows building.
	 */
	public boolean canBeBroken(Block block) {
		return (kitType.getId().equals("SPLEEF") && (block.getType() == Material.SNOW_BLOCK || block.getType() == Material.GRASS || block.getType() == Material.DIRT)) || placedBlocks.contains(block.getLocation().toVector().toBlockVector());
	}

}