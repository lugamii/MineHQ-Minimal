# Arena Grid

The arena grid provides an easy way to manage `Arena` instances present in a Minecraft world.

Although the arena grid also consists of a menu / command system for admin management, this document only focuses on the programmatic implementation.
The references provided as the end of the file link to a, although very messy, working implementation of this document.

An `ArenaSchematic` is equivilant to a .schematic file present in the WorldEdit schematics folder. For example, an `ArenaSchematic` named
`Courtyard` will have its schematic file available at `plugins/WorldEdit/schematics/Courtyard.schematic`.

# Primer

* Know the difference between an `Arena` and an `ArenaSchematic`.
* General familiarity with the `net.lugami.practice.arena` package.

# Intro

An `ArenaGrid` handles the process of pasting N instances of an `ArenaSchematic` in a world. The `ArenaGrid` can handle creating and deleting `Arena` instances.
Ideally the method signatures exposed by `ArenaGrid` would be:

```
int getCopies(ArenaSchematic schematic);
void scaleCopies(Player initiator, ArenaSchematic schematic, int copies);
```

Calling `scaleCopies` where copies is greater than the current number of copies will create arenas, where as copies being
less will cause arenas to be deleting (starting with the highest numbered copy)

# Creating Arenas

When an `Arena` is created (as part of a `scaleCopies` call), we paste it in its proper location (see Scaling section) with the WorldEdit API. After the arena is pasted, we must detect its spawn points, bounds, etc. and register
the `Arena` with the `ArenaHandler`. After pasting a schematic (see line 8), we 'scan' the schematic to look for certain blocks marking
these points. Team 1 / team 2 spawn locations are marked by a fence with a player skull on top of it. The location's pitch/yaw should be
derived from the rotation of the skull (See [here](https://github.com/FrozenOrb/PotPvP-player/blob/master/src/main/java/net/frozenorb/potpvp/api/map/BaseGrid.java#L233).)
What team a skull gets mapped to is irrelevant, as team 1 / team 2 are only used internally - players are not told which team they are on.
The spectator spawn location are marked by a fence with a skeleton skull on top of it. The arena bounds are the same as the schematic bounds.

If any of the locations required are not found, the `Arena` creation should fail and the player (see initiator param in `scaleCopies` method) should
be informed.

# Scaling

All `Arena` instances exist in the region of the arena where `x >= 1_000 && z >= 1_000`. Previously `Arena`s were stored in lines,
with one line per `ArenaSchematic`. This worked well, however finding out where each `ArenaSchematic` line started proved to be difficult.

The world created under the previous arena grid looked similar to the following picture:

```
  X ----->

Z   Courtyard 1     Courtyard 2     Courtyard 3     Courtyard 4
|  
|   Mario 1   Mario 2   Mario 3     Mario 4   Mario 5
v
    Mesa 1    Mesa 2    Mesa 3    Mesa 4   Mesa 5

```

This caused arena instances to get 'out of alignment' with the other arenas, which caused some confusion. Perhaps defining a max arena size and doing
static spacing will (at the expensive of disk space) reduce the code complexity.

# Technical Notes

* Trying to abstract the system into `BaseGrid` and `StandardMapGrid` and using generics (as seen in the references) didn't work well.
* Clients shouldn't have to deal with the process of loading/saving schematics, the save files (see `ArenaHandler.java`), or anything else. (Previously this was not the case)

# References

[1](https://github.com/FrozenOrb/PotPvP-player/blob/master/src/main/java/net/frozenorb/potpvp/player/map/MapHandler.java)
[2](https://github.com/FrozenOrb/PotPvP-player/blob/master/src/main/java/net/frozenorb/potpvp/player/map/StandardMapGrid.java)
[3](https://github.com/FrozenOrb/PotPvP-player/blob/master/src/main/java/net/frozenorb/potpvp/api/map/BaseGrid.java)
