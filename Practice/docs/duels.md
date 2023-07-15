# Duels

Duels, previously referred to as invites, let players / parties start matches with other players / parties.

A previous (bad) implementation exists [here](https://github.com/FrozenOrb/PotPvP/tree/master/potpvp-player/src/main/java/net/frozenorb/potpvp/player/invite), however it should only be used for reference for lapses in documentation. See Technical Notes section for more.

# Intro

Duels can exist in player vs player or party vs party variations. When sent via
/duel, a message is sent to the target of the duel (either a player or a party) informing them of the duel and offering clickable buttons to
accept the duel. Duels cannot be explicitly declined, only ignored. When mutual duels have been sent (ex A sends a duel invite to B, B then
sends A a duel invite for the same kit type), the duel invite is automatically accepted. Duel invites expire after 30 seconds, and should respect the [setting](https://github.com/FrozenOrb/PotPvP-SI/blob/master/src/main/java/net/frozenorb/potpvp/setting/Setting.java#L64) available to some players. Players can only send/receive invites when they're in a player (when `Practice.getInstance().getMatchHandler().isPlayingOrSpectatingMatch(Player)` is false)

# Commands

The two commands used for duels are `/duel` and `/accept`

`/duel <target>`:
Sends a duel invite to another player/party. Requires kit type input to send duel invite.

* Opens a kit selection menu (`SelectKitTypeMenu`) to select a type to duel with
* Ensure target is not playing in / spectating a match
* Ensure duels are enabled (need to build toggle for this
* If player is in a party
  * If the player is the leader, create a duel from party -> target party
     * Create a duel invite between parties, inform sender party (only leader will know invite was sent otherwise) and target party
     * Don't allow more than 1 invite per 30 seconds per player/target combo (kit type is ignored for this)
  * If the player is not the leader send a no permission message. (Perhaps in the future we can suggest the leader start a duel similar to how we do party invites)
* If the player is not in a party
  * Don't allow more than 1 invite per 30 seconds per player/target combo (kit type is ignored for this)
  * Create a duel invite like normal, inform target player
  
`/accept <target>`:
Accepts an invite from another player/party. No kit type input is required, will accept whatever type of invite was sent.

* If sender is in a party
  * If the player is the leader
    * Look for duel invite sent from target party to sender party and accept
  * If the player is not the leader send a no permission message
* If the sender is not in a party
  * Look for an invite from a player to this player and accept

Reference: [1](https://github.com/FrozenOrb/PotPvP/blob/master/potpvp-player/src/main/java/net/frozenorb/potpvp/player/invite/InviteHandler.java#L304),
[2](https://github.com/FrozenOrb/PotPvP/blob/master/potpvp-player/src/main/java/net/frozenorb/potpvp/player/invite/InviteHandler.java#L170)

# Menus

Menus used to select kit types for duels (/duel) are the standard `SelectKitType` menu.

The only other menu present is the 'other parties' menu. This menu is shown to party leaders and displays all other parties on the server,
paginated and sorted by size. The menu contains one button per other party online, whose lore is as follows:
```
&e&l%leader%'s party

&b*%leader%
&a%member 1%
&a%member 2%
&a%member 3%
[ and so on ]

&a&lCLICK HERE &6to send a duel request.
```
When clicked a duel request is sent to the target party, in the same way `/duel <party leader>` works.

This menu should live update.

Reference: [1](https://github.com/FrozenOrb/PotPvP/blob/master/potpvp-player/src/main/java/net/frozenorb/potpvp/player/invite/button/otherparties/ChallengePartyButton.java),
[2](https://github.com/FrozenOrb/PotPvP/blob/master/potpvp-player/src/main/java/net/frozenorb/potpvp/player/invite/menu/OtherPartiesMenu.java)

# Technical Notes

* qLib's menu system has a built in pagination system, visible [here](https://github.com/FrozenOrb/qLib/tree/master/src/main/java/net/frozenorb/qlib/menu/pagination).
* Previously we tried to use generics to make one `DuelInvite` class which lets the target/sender type be specified,
which did not wok well - likely could have been done properly, but similar to queues it's likely easier to avoid using generics
* Methods like `Set<DuelInvite> getDuelInvites(Party target)` and `DuelInvite getDuelInvite(Party sender, Party target)` (and the same for individual players) would integrate nicely with the rest of PotPvP.
