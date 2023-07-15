package net.minecraft.server;

import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.player.PlayerPearlRefundEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.Gate;
import org.bukkit.material.Openable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.spigotmc.SpigotConfig;

import java.util.Set;

// CraftBukkit start

public class EntityEnderPearl extends EntityProjectile {

    private Location lastValidTeleport;
    private Item toRefundPearl = null;
    private EntityLiving c;

    private static Set<Block> PROHIBITED_PEARL_BLOCKS = Sets.newHashSet(Block.getById(85),
            Block.getById(107)
    );

    public EntityEnderPearl(World world) {
        super(world);
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    public EntityEnderPearl(World world, EntityLiving entityliving) {
        super(world, entityliving);
        this.c = entityliving;
        this.loadChunks = world.paperSpigotConfig.loadUnloadedEnderPearls; // PaperSpigot
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (SpigotConfig.pearlThroughGatesAndTripwire) {
            Block block = this.world.getType(movingobjectposition.b, movingobjectposition.c, movingobjectposition.d);

            if (block == Blocks.TRIPWIRE) {
                return;
            } else if (block == Blocks.FENCE_GATE) {
                BlockIterator bi = null;

                try {
                    Vector l = new Vector(this.locX, this.locY, this.locZ);
                    Vector l2 = new Vector(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
                    Vector dir = new Vector(l2.getX() - l.getX(), l2.getY() - l.getY(), l2.getZ() - l.getZ()).normalize();
                    bi = new BlockIterator(this.world.getWorld(), l, dir, 0, 1);
                } catch (IllegalStateException ex) {
                    // ignore
                }

                if (bi != null) {
                    boolean open = true;
                    boolean hasSolidBlock = false;

                    while (bi.hasNext()) {
                        org.bukkit.block.Block b = bi.next();

                        if (b.getType().isSolid() && b.getType().isOccluding()) {
                            hasSolidBlock = true;
                        }

                        if (b.getState().getData() instanceof Gate && !((Gate) b.getState().getData()).isOpen()) {
                            open = false;
                            break;
                        }
                    }

                    if (open && !hasSolidBlock) {
                        return;
                    }
                }
            }
        }

        if (movingobjectposition.entity != null) {
            if (movingobjectposition.entity == this.c) {
                return;
            }

            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), 0.0F);
        }

        // PaperSpigot start - Remove entities in unloaded chunks
        if (inUnloadedChunk && world.paperSpigotConfig.removeUnloadedEnderPearls) {
            die();
        }
        // PaperSpigot end

        for (int i = 0; i < 32; ++i) {
            this.world.addParticle("portal", this.locX, this.locY + this.random.nextDouble() * 2.0D, this.locZ, this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
        }

        if (!this.world.isStatic) {
            if (this.getShooter() != null && this.getShooter() instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) this.getShooter();

                if (entityplayer.playerConnection.b().isConnected() && entityplayer.world == this.world) { // MineHQ
                    // CraftBukkit start - Fire PlayerTeleportEvent
                    if (this.lastValidTeleport != null) {
                        org.bukkit.craftbukkit.entity.CraftPlayer player = entityplayer.getBukkitEntity();
                        org.bukkit.Location location = this.lastValidTeleport;
                        location.setPitch(player.getLocation().getPitch());
                        location.setYaw(player.getLocation().getYaw());

                        PlayerTeleportEvent teleEvent = new PlayerTeleportEvent(player, player.getLocation(), location, PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
                        Bukkit.getPluginManager().callEvent(teleEvent);

                        if (!teleEvent.isCancelled() && !entityplayer.playerConnection.isDisconnected()) {
                            if (this.getShooter().am()) {
                                this.getShooter().mount((Entity) null);
                            }

                            entityplayer.playerConnection.teleport(teleEvent.getTo());
                            this.getShooter().fallDistance = 0.0F;
                            CraftEventFactory.entityDamage = this;
                            this.getShooter().damageEntity(DamageSource.FALL, 5.0F);
                            CraftEventFactory.entityDamage = null;
                        }
                        // CraftBukkit end
                    } else {
                        Bukkit.getPluginManager().callEvent(new PlayerPearlRefundEvent(entityplayer.getBukkitEntity()));
                    }
                }
            }

            this.die();
        }
    }

    @Override
    public void h() {
        EntityLiving shooter = this.getShooter();
        if (shooter != null && !shooter.isAlive()) {
            this.die();
        } else {
            AxisAlignedBB newBoundingBox = AxisAlignedBB.a(this.locX - 0.3D, this.locY - 0.05D, this.locZ - 0.3D, this.locX + 0.3D, this.locY + 0.5D, this.locZ + 0.3D);
            if (!this.world.boundingBoxContainsMaterials(this.boundingBox.grow(0.25D, 0.0D, 0.25D), PROHIBITED_PEARL_BLOCKS) && this.world.getCubes(this, newBoundingBox).isEmpty()) {
                this.lastValidTeleport = this.getBukkitEntity().getLocation();
            }

            org.bukkit.block.Block block = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
            Material typeHere = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)).getType();
            if (typeHere.toString().contains("STAIR") || typeHere.toString().contains("STEP") || typeHere.toString().contains("WALL") || typeHere.toString().contains("SLAB")) {
                this.lastValidTeleport = this.getBukkitEntity().getLocation();
            }

            if (typeHere == Material.FENCE_GATE && ((Openable)block.getState().getData()).isOpen()) {
                this.lastValidTeleport = this.getBukkitEntity().getLocation();
            }

            super.h();
        }

    }

    //    @Override
//    public void h() {
//        EntityLiving shooter = this.getShooter();
//
//        if (shooter != null && !shooter.isAlive()) {
//            this.die();
//        } else {
//            AxisAlignedBB newBoundingBox = AxisAlignedBB.a(this.locX - 0.3D, this.locY - 0.05D, this.locZ - 0.3D, this.locX + 0.3D, this.locY + 0.5D, this.locZ + 0.3D);
//
//            if (!this.world.boundingBoxContainsMaterials(this.boundingBox.grow(0.25D, 0D, 0.25D), PROHIBITED_PEARL_BLOCKS) && this.world.getCubes(this, newBoundingBox).isEmpty()) {
//                this.lastValidTeleport = getBukkitEntity().getLocation();
//            } else {
//                Material typeHere = this.world.getWorld().getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)).getType();
//
//                if (typeHere == Material.STEP || typeHere == Material.WOOD_STEP) {
//
//                }
//            }
//
//            super.h();
//        }
//    }

    public Item getToRefundPearl() {
        return this.toRefundPearl;
    }

    public void setToRefundPearl(Item pearl) {
        this.toRefundPearl = pearl;
    }
}
