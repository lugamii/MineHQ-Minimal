package net.lugami.bridge.bukkit.commands.disguise.menu;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import net.lugami.qlib.menu.Button;
import net.lugami.qlib.menu.pagination.PaginatedMenu;
import net.lugami.qlib.util.ItemBuilder;
import net.minecraft.server.v1_7_R4.GameProfileSerializer;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import net.lugami.bridge.BridgeGlobal;
import net.lugami.bridge.bukkit.Bridge;
import net.lugami.bridge.global.disguise.DisguisePlayer;
import net.lugami.bridge.global.disguise.DisguiseProfile;
import net.lugami.bridge.global.ranks.Rank;
import net.lugami.bridge.global.util.mojang.GameProfileUtil;

import java.util.*;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class DisguiseSkinMenu extends PaginatedMenu {

    private final String nickName;
    private final Rank rank;
    private final boolean skinCmd;
    private final boolean real;

    private static final GameProfile QUESTION_MARK_SKIN, STEVE_SKIN, ALEX_SKIN, DSKIN;

    static {
        QUESTION_MARK_SKIN = new GameProfile(UUID.randomUUID(), "__Q_MARK__");
        QUESTION_MARK_SKIN.getProperties().put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYzMjM1NTQwMTk5MSwKICAicHJvZmlsZUlkIiA6ICJkNTI0NjJmOTZlZjA0OThmODhhYzg4ZDI3ZjMyOGEzMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJuaWd3YWciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ViYjQ3YTY1NDcyYjI4MjRmNDNjNDY0Yzc0NmI2Y2I3NGNmYmU5NWJkODk1MTM2Yjg3MWQ3ZTk3ZGM1NDY4MyIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjM0MGMwZTAzZGQyNGExMWIxNWE4YjMzYzJhN2U5ZTMyYWJiMjA1MWIyNDgxZDBiYTdkZWZkNjM1Y2E3YTkzMyIKICAgIH0KICB9Cn0=", "jCNxGyWZtXrpgp9iA9z9/cpAkPcbXUz3QyQbBL9y58QaAvwpCC5t/0UReOBppIwS3Kye5JNf4W/sgk37+dSwej9ioiXAGQNQo56jCqaioaq4kKlaFls+dgbw2AJticzWsJicT70omH9O/NHV2kLP1RxPSQBQwkgnmmYBUtMYENf5S/zzUddZtbr3g0G5elpgwGuDaIJiNNdNTGwatUNfUlNLV/a9Z1nAniwfOUWul2U0tGTLnmrSyMLQe6auLyF/ddMneU1ecVTqjqUQZ8USOjva8N9tryRVGgkHujmMgd8zkOlU/FPxdLZeJoii4KQoSHznHx5IJplGvwEwUDR2cgNnzAzl7utMMRTC715TiLtcayqG3VJj1qRERt3uzDCBAidDcghwB+7GWkzZQ5Fq3P+pOGQdaOLyfiOoJojcKskPjAeGyLMq1rZd9VQ1RT/CuCCTgNhuIH8Ro4uyXXi18r63AMuHEhbzsD5wP+Okm6Z3SGzPJPOWQwQxHnmnO+6l11FZaMyJx68w90QCbmbiWXBDC89MVVLkl3rvUXmXKTr2N0HdjRY2uo4myqKugfR4ltT5jNMHLBy2yAg/dt/RminWB3SFbqFaK9iNam2XpOBW+q9Ihrmd81dfLhl663ORN+ikkiZzO2AZozvWCNx36snAPpMeBhs4f4Aehux+DH4="));

        GameProfileUtil.getSkinCache().put(QUESTION_MARK_SKIN.getName().toLowerCase(), QUESTION_MARK_SKIN);

        DSKIN = new GameProfile(UUID.randomUUID(), "__D_SKIN__");
        DSKIN.getProperties().put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYwNTgwNjE3MDgyNSwKICAicHJvZmlsZUlkIiA6ICIzY2FjYjhkY2YyMzk0ZDgwOTZiMWIyMmUzNjQ2MDlmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJjcmVlcGVyX2dvZDIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE2ZTRjNTY1NDhiZmM0NjRmZGQ1NmJkMGViMmE2NGEzZWNiMTY4MWVhMDk5MWM0ZWY3NzFmNmQxMThiNGMzYyIKICAgIH0KICB9Cn0=", "FpKriqMG97l8aCmUfsa0Dq8ArVPCFiw6Mt2HwIwxHKUXKfhEK074MyeZwV5MQKaK/kEkqgYrCVK1Upm4y+SIp1znmoz9Qdb9lfdctXwznjwNW6x60qP9u5Wadg9T2CBw3KkogSTlFJpeiKnOb9bT4IezQgu8fL1Or5yX5LGkxrcZpDpCGJgENmLPdwoSaZxfZbOEnEGKRNrhqnPm29ScuUAf3JtQGccElXlJ4wBH02ZbuoFA75mnbCMixNmgiBpBIhqzAq7at+YrFqU1Nn6MYokFP91EbQvfxHP9vNN5ANJVDuhUPHNAvwK3M4XYthJ01Vm0oCRO/WqKDZCepV5j3pAZHKGA9ubeKAA2zTWwILxjgx1BaKBYO9idTdhI5RgsK78hg2ICD6ySZ7S+WFLYx7gTHSgp+TFaMsqeHHy5XGUHFUj7FOAktTu/lMs9KjK8n1FM5V3JDvAU5c7xuuErQ3HoYc5wO+Wm2gj9i5LBucU9qWlVSO51W1guwTSD9U8kzuw8Z4xoikF6of0/ZtmMjUneOGnzDqV2wNKnDbV/V5ASSVDjERvQCyK44YS1w9oO/p3ROjdhuN+JWP4zPCmfFdDxhgLcEhrTBcm7kBNx+b6agudfz4i6i7dAPN2W5AZ2RKbc60kkJncxtgfvfLk3Gw1j8TLAT1EjyDnhcMRitXY="));

        STEVE_SKIN = new GameProfile(UUID.randomUUID(), "Steve");
        STEVE_SKIN.getProperties().put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYwNTYxMTkyMjAyNywKICAicHJvZmlsZUlkIiA6ICJiOTgzZmU5YWJjMWI0MTkwYjIxZTUxZmU0Yjg3OTcxNCIsCiAgInByb2ZpbGVOYW1lIiA6ICI5Z3lhciIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82ZDNiMDZjMzg1MDRmZmMwMjI5Yjk0OTIxNDdjNjlmY2Y1OWZkMmVkNzg4NWY3ODUwMjE1MmY3N2I0ZDUwZGUxIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "nquy5XsHlcsQn0QTFW0vOVnodhCizG3VlfPtqgkqmTliNsuWd+GxdUV6u00eOhmYrkHOtD52eWF0tYG+j/BYUBwE2SvZHt3V7OjAO3OzMy8sNzuyymEfzoqFXB2+IBAAbWi54TZHI4l03wtDFuUNBhPZ/q0h9KmasFv8zTfCht+TEThmp7gjCkmAs6Tkr1jqAQyQfCQCQwBN0a8Rx0NzsGJAOvQFmfdksxuLzckR8abt9Qb39QXulFAKqkNoVYajvRxG21WTELgJEM+nuVInt0QT91t6Al1E7xExCbrOJITomk2CLAHSJzTx0QBYmKMbPeuXcR1jT50FA4qzqY5LsmmVWb1PkaucFQ2+tHHKncNg44XMju2PaNNLFek39AhOp4FnyLHBK5RuCJEcv0ybcNyCxFmKBSb5Ryv+XxsQQjzcT0fSc5png0NvBVknk4KnWkuimMJp84nBSzJ9htS38mOZWdFcj61ulqk99+7wJfqO8OgtOlfn4Hpk6rbVcFtARnW8TQIPcNzX6/B385fARO5VwFmts7UVxRGenQOJwzbezGPKBTfQyODpPxX2DYTR4v8fGfVpaO3ZReq0vDLvl/x5CpyFISJhorfZSIiAXJeRRKj8qHr6eM1lqzkzwu6tUV8nITzg/QxVuOddik01HJritR/iTgfrCQNI2fjhJY8="));

        GameProfileUtil.getSkinCache().put(STEVE_SKIN.getName().toLowerCase(), STEVE_SKIN);

        ALEX_SKIN = new GameProfile(UUID.randomUUID(), "Alex");
        ALEX_SKIN.getProperties().put("textures", new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYwNTYxMTgxNjQ1NiwKICAicHJvZmlsZUlkIiA6ICIwNDQ4NzNiYTMzZDI0Y2Q4YTQ1M2M4ODkwYTFjODM4MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJMQVNEIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNiNjBhMWY2ZDU2MmY1MmFhZWJiZjE0MzRmMWRlMTQ3OTMzYTNhZmZlMGU3NjRmYTQ5ZWEwNTc1MzY2MjNjZDMiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "u0w6ba9InsC1Nj1xRxO5Jl6XsiuKEP0oMDQEBGoEEZJ8+thETE2Ics2lRY0jTKkboJV+7FwJunkYCJyoR6sRKYsXW6z5FOxsvCu9biAwIEcmRXljBUnIMzhefCqovZJ07AjA0rM8aKSH9V47BbpfGzBgSaLukdem0VpvV4LfZFIGTvqEZD6QnlYDQUJNEDaIac1X4xOJlPXzXV0d7EwSZSa6zKv6nswgbsp3aWleJoUoXhjqB3Em5wbdJbbvDdr0v7PPPYVzgS2sqD+RpUap21jG8teb4Wan4qjZQLTbmRxJE68IMaaylygwJFinxViZw3ubV7uTz26BSAhvOQ2wLpcNtTqqr+8atIA1+C5ijhBmQLEH6U2aYb7K+qF83YhxTzFeeoHcdoDNHC+7LDgEFEnM0hpIuHPTho2uTJtvAHx2iuLd/Kzau+EFyZDlKHowN7UW6LwNXRldTPr5DHBEuAMTiSGBivYZi778Hx8b3fpb6I7rUN5o4l3RdktCPlrElPnmEYXBGH+4ZLkeNxajCMxvm0WJFEouQcpnxKmON4z9Q6bduFdSqgm+huGACPhMG41J2f6fsnTOFKQDluyekF1ur3KoNCdqEYMDqXHmSobRaaKhvoclQuO1oxjFkFAiKox5cOAOf+pmhwbgq9a26n6MzvYvLb+tVXkbN9V6ewQ="));

        GameProfileUtil.getSkinCache().put(ALEX_SKIN.getName().toLowerCase(), ALEX_SKIN);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return ChatColor.DARK_GRAY + "Pick a skin";
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 36;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        HashMap<Integer, Button> buttons = Maps.newHashMap();

        int slot = 0;
        for(DisguiseProfile profile : BridgeGlobal.getDisguiseManager().getDisguiseProfiles().values()) {
            buttons.put(slot++, new Button() {

                @Override
                public String getName(Player player) {
                    return null;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return null;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    ItemStack stack = ItemBuilder.of(Material.SKULL_ITEM)
                            .data((short) 3)
                            .name(profile.getDisplayName() != null ? ChatColor.translateAlternateColorCodes('&', profile.getDisplayName()) : ChatColor.BLUE + profile.getName())
                            .setLore(Collections.singletonList("")).build();

                    net.minecraft.server.v1_7_R4.ItemStack item = CraftItemStack.asNMSCopy(stack);

                    GameProfile gameProfile = GameProfileUtil.getSkinCache().get(profile.getName().toLowerCase());
                    NBTTagCompound nbtTagCompound = new NBTTagCompound();

                    if (gameProfile == null) {
                        gameProfile = new GameProfile(profile.getSkin().getProfileUuid(), profile.getName());
                        gameProfile.getProperties().put("textures", profile.getSkin().getProperty());
                        GameProfileUtil.getSkinCache().put(profile.getName().toLowerCase(), gameProfile);
                    }

                    GameProfileSerializer.serialize(nbtTagCompound, gameProfile);
                    NBTTagCompound itemNbtTag = item.getTag();
                    itemNbtTag.set("SkullOwner", nbtTagCompound);
                    stack = CraftItemStack.asCraftMirror(item);

                    return stack;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.closeInventory();
                    Button.playSuccess(player);
                    disguise(player, rank, profile.getSkinName(), nickName, profile.getDisplayName() != null ? ChatColor.translateAlternateColorCodes('&', profile.getDisplayName()) : profile.getSkinName(), profile.getName());
                }
            });
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        HashMap<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(1, new Button() {

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                ItemStack stack = ItemBuilder.of(Material.SKULL_ITEM)
                        .data((short) 3)
                        .name(ChatColor.BLUE + "Your Own")
                        .setLore(Collections.singletonList("")).build();

                net.minecraft.server.v1_7_R4.ItemStack item = CraftItemStack.asNMSCopy(stack);
                NBTTagCompound nbtTagCompound = new NBTTagCompound();


                GameProfileSerializer.serialize(nbtTagCompound, ((CraftPlayer) player).getProfile());

                NBTTagCompound itemNbtTag = item.getTag();
                itemNbtTag.set("SkullOwner", nbtTagCompound);
                stack = CraftItemStack.asCraftMirror(item);

                return stack;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                Button.playSuccess(player);
                DisguisePlayer disguisePlayer = BridgeGlobal.getDisguiseManager().getDisguisePlayers().get(player.getUniqueId());
                disguise(player, rank, player.getName(), nickName, disguisePlayer != null ? disguisePlayer.getName() : player.getName());
            }
        });

        buttons.put(2, new Button() {

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                ItemStack stack = ItemBuilder.of(Material.SKULL_ITEM)
                        .data((short) 3)
                        .name(ChatColor.BLUE + "Random")
                        .setLore(Collections.singletonList("")).build();

                net.minecraft.server.v1_7_R4.ItemStack item = CraftItemStack.asNMSCopy(stack);
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                GameProfileSerializer.serialize(nbtTagCompound, QUESTION_MARK_SKIN);
                NBTTagCompound itemNbtTag = item.getTag();
                itemNbtTag.set("SkullOwner", nbtTagCompound);
                stack = CraftItemStack.asCraftMirror(item);

                return stack;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                Button.playSuccess(player);

                DisguiseProfile profile = BridgeGlobal.getDisguiseManager().getRandomDisguiseProfile();

                if(profile == null) {
                    player.sendMessage(ChatColor.RED + "Failed to find random disguise profile.");
                    return;
                }

                disguise(player, rank, profile.getSkinName(), nickName, profile.getDisplayName() != null ? ChatColor.translateAlternateColorCodes('&', profile.getDisplayName()) : profile.getSkinName());
            }
        });

        buttons.put(3, new Button() {

            @Override
            public String getName(Player player) {
                return ChatColor.BLUE + "Pick a Name";
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SIGN;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                Button.playSuccess(player);

                ConversationFactory factory = new ConversationFactory(Bridge.getInstance())
                        .withModality(true)
                        .withPrefix(new NullConversationPrefix())
                        .withFirstPrompt(new StringPrompt() {

                            @Override
                            public String getPromptText(ConversationContext context) {
                                return ChatColor.YELLOW + "Enter a name of a player using your desired skin:";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext context, String input) {
                                if(!Pattern.compile("^\\w{1,16}$").matcher(input).matches()) {
                                    player.sendMessage(ChatColor.RED + "Invalid username: " + input);

                                    String realName = GameProfileUtil.getRealName(input);

                                    new DisguiseSkinMenu(nickName, rank, skinCmd, realName != null).openMenu(player);
                                    return Prompt.END_OF_CONVERSATION;
                                }

                                disguise(player, rank, input, nickName, input);
                                return Prompt.END_OF_CONVERSATION;
                            }
                        })

                        .withLocalEcho(false)
                        .withEscapeSequence("/no")
                        .withTimeout(10)
                        .thatExcludesNonPlayersWithMessage("Go away evil console!");

                player.beginConversation(factory.buildConversation(player));
            }
        });

        buttons.put(4, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " "));

        buttons.put(5, new Button() {

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                ItemStack stack = ItemBuilder.of(Material.SKULL_ITEM)
                        .data((short) 3)
                        .name(ChatColor.BLUE + nickName)
                        .setLore(Collections.singletonList("")).build();

                net.minecraft.server.v1_7_R4.ItemStack item = CraftItemStack.asNMSCopy(stack);
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                GameProfileSerializer.serialize(nbtTagCompound, DSKIN);
                NBTTagCompound itemNbtTag = item.getTag();
                itemNbtTag.set("SkullOwner", nbtTagCompound);
                stack = CraftItemStack.asCraftMirror(item);

                return stack;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                Button.playSuccess(player);

                int version = ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion();

                disguise(player, rank, real ? nickName : version == 47 ? ALEX_SKIN.getName() : STEVE_SKIN.getName(), nickName, nickName);
            }
        });

        buttons.put(6, new Button() {

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                ItemStack stack = ItemBuilder.of(Material.SKULL_ITEM)
                        .data((short) 3)
                        .name(ChatColor.BLUE + "Steve")
                        .setLore(Collections.singletonList("")).build();

                net.minecraft.server.v1_7_R4.ItemStack item = CraftItemStack.asNMSCopy(stack);
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                GameProfileSerializer.serialize(nbtTagCompound, STEVE_SKIN);
                NBTTagCompound itemNbtTag = item.getTag();
                itemNbtTag.set("SkullOwner", nbtTagCompound);
                stack = CraftItemStack.asCraftMirror(item);

                return stack;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                Button.playSuccess(player);
                disguise(player, rank, STEVE_SKIN.getName(), nickName, "Steve");
            }
        });

        buttons.put(7, new Button() {

            @Override
            public String getName(Player player) {
                return null;
            }

            @Override
            public List<String> getDescription(Player player) {
                return null;
            }

            @Override
            public Material getMaterial(Player player) {
                return null;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
                ItemStack stack = ItemBuilder.of(Material.SKULL_ITEM)
                        .data((short) 3)
                        .name(ChatColor.BLUE + "Alex")
                        .setLore(Collections.singletonList("")).build();

                net.minecraft.server.v1_7_R4.ItemStack item = CraftItemStack.asNMSCopy(stack);
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                GameProfileSerializer.serialize(nbtTagCompound, ALEX_SKIN);
                NBTTagCompound itemNbtTag = item.getTag();
                itemNbtTag.set("SkullOwner", nbtTagCompound);
                stack = CraftItemStack.asCraftMirror(item);

                return stack;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                player.closeInventory();
                Button.playSuccess(player);
                disguise(player, rank, ALEX_SKIN.getName(), nickName, "Alex");
            }
        });

        return buttons;
    }

    private void disguise(Player player, Rank rank, String skin, String name, String skinName) {
        this.disguise(player, rank, skin, name, skinName, null);
    }

    private void disguise(Player player, Rank rank, String skin, String name, String skinName, String profileName) {
        try {
            //                disguise(player, rank, player.getName(), nickName, disguisePlayer != null ? disguisePlayer.getName() : player.getName());
            DisguisePlayer disguisePlayer = new DisguisePlayer(player.getName());
            disguisePlayer.setDisguiseRank(rank);
            disguisePlayer.setDisguiseName(name);
            disguisePlayer.setDisguiseSkin(skin);

            if(BridgeGlobal.getDisguiseManager().disguise(player, disguisePlayer, profileName,false, true, !this.skinCmd)) {
                player.sendMessage(ChatColor.GREEN + "Success! You now look like " + this.rank.getPrefix() + this.rank.getColor() + this.nickName + ChatColor.GREEN + (!name.equals(skin) && !name.equals(ChatColor.stripColor(skinName)) ? " (in the skin of " + ChatColor.YELLOW + ChatColor.stripColor(skinName) + ChatColor.GREEN + ")" : "") + "!");

                String realName = GameProfileUtil.getRealName(this.nickName);
                if (realName != null) {
                    player.sendMessage(ChatColor.RED + this.nickName + " is an existing Minecraft player, so if they log on for the first time as you're disguised, you will be kicked.");
                }

                String realSkin = GameProfileUtil.getRealName(skin);
                if (realSkin == null && BridgeGlobal.getDisguiseManager().getDisguiseProfiles().values().stream().noneMatch(p -> p.getSkinName().equalsIgnoreCase(skin)) || skin.equals(STEVE_SKIN.getName()) || skin.equals(ALEX_SKIN.getName())) {
                    player.sendMessage(ChatColor.YELLOW + "Note: You will look like " + (skin.equals("Alex") ? "Alex" : "Steve") + " since the account \"" + skin + "\" does not exist.");
                }

                /*
                String realName = GameProfileUtil.getRealName(this.nickName);
                if (realName != null) {
                    player.sendMessage(ChatColor.RED + this.nickName + " is an existing Minecraft player, so if they log on for the first time as you're disguised, you will be kicked.");
                } else {
                    String realSkin = GameProfileUtil.getRealName(skin);
                    if (realSkin != null) {
                        player.sendMessage(ChatColor.YELLOW + "Note: You will look like Steve since the account \"" + skin + "\" does not exist.");
                    }
                }
                 */
            }
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Something went wrong while disguising you! Please contact a staff member or any online developer.");
        }
    }
}