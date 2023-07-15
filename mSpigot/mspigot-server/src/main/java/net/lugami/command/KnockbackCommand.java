package net.lugami.command;

import net.lugami.knockback.CraftKnockback;
import net.lugami.knockback.Knockback;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.spigotmc.SpigotConfig;

import java.util.Arrays;

public class KnockbackCommand extends Command {

    public KnockbackCommand() {
        super("bridgeontop");

        this.setAliases(Arrays.asList("knockback", "kb"));
        this.setUsage(StringUtils.join(new String[]{
                ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 35),
                ChatColor.RED + "/kb list",
                ChatColor.RED + "/kb create <profile>",
                ChatColor.RED + "/kb delete <profile>",
                ChatColor.RED + "/kb update <profile> <f> <h> <v> <vl> <eh> <ev>",
                ChatColor.RED + "/kb setfriction <profile> <f>",
                ChatColor.RED + "/kb sethorizontal <profile> <h>",
                ChatColor.RED + "/kb setvertical <profile> <v>",
                ChatColor.RED + "/kb setverticallimit <profile> <vl>",
                ChatColor.RED + "/kb setextrahorizonal <profile> <eh>",
                ChatColor.RED + "/kb setextravertical <profile> <ev>",
                ChatColor.RED + "/kb setglobal <profile>",
                ChatColor.RED + "/kb autowtap <profile> <enabled>",
                ChatColor.RED + "/kb kohi <profile> <enabled>",
                ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 35)
        }, "\n"));
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Unknown command.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        Label_2302: {
            String lowerCase;
            switch (lowerCase = args[0].toLowerCase()) {
                case "create": {
                    if (args.length > 1) {
                        String name = args[1];
                        for (Knockback profile2 : SpigotConfig.kbProfiles) {
                            if (profile2.getName().equalsIgnoreCase(name)) {
                                sender.sendMessage(ChatColor.RED + "A profile with that name already exists.");
                                return true;
                            }
                        }
                        CraftKnockback profile3 = new CraftKnockback(name);
                        SpigotConfig.kbProfiles.add(profile3);
                        SpigotConfig.saveKnockback();
                        sender.sendMessage(ChatColor.GOLD + "New profile created.");
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "Usage: /kb create <name>");
                    return true;
                }
                case "delete": {
                    if (args.length <= 1) {
                        sender.sendMessage(ChatColor.RED + "Usage: /kb delete <name>");
                        return true;
                    }
                    String name = args[1];
                    if (SpigotConfig.globalKbProfile.getName().equalsIgnoreCase(name)) {
                        sender.sendMessage(ChatColor.RED + "You can't delete the active global knockback profile.");
                        return true;
                    }
                    if (SpigotConfig.kbProfiles.removeIf(profile -> profile.getName().equalsIgnoreCase(name))) {
                        SpigotConfig.saveKnockback();
                        sender.sendMessage(ChatColor.RED + "Deleted profile.");
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                    }
                    return true;
                }
                case "setextravertical": {
                    if (args.length != 3) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    Knockback profile4 = SpigotConfig.getKnockbackByName(args[1]);
                    if (profile4 == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }
                    double extraVertical;
                    try {
                        extraVertical = Double.parseDouble(args[2]);
                    }
                    catch (Exception ex) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    profile4.setExtraVertical(extraVertical);
                    SpigotConfig.saveKnockback();
                    sender.sendMessage(ChatColor.GREEN + "Set " + profile4.getName() + "'s Extra Vertical to " + extraVertical + ".");
                    return true;
                }
                case "update": {
                    if (args.length != 8) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    Knockback profile4 = SpigotConfig.getKnockbackByName(args[1]);
                    if (profile4 == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }
                    profile4.setFriction(Double.parseDouble(args[2]));
                    profile4.setHorizontal(Double.parseDouble(args[3]));
                    profile4.setVertical(Double.parseDouble(args[4]));
                    profile4.setVerticalLimit(Double.parseDouble(args[5]));
                    profile4.setExtraHorizontal(Double.parseDouble(args[6]));
                    profile4.setExtraVertical(Double.parseDouble(args[7]));
                    SpigotConfig.saveKnockback();
                    sender.sendMessage(ChatColor.GREEN + "Updated values.");
                    return true;
                }
                case "setverticallimit": {
                    if (args.length != 3) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    Knockback profile4 = SpigotConfig.getKnockbackByName(args[1]);
                    if (profile4 == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }
                    double verticalLimit;
                    try {
                        verticalLimit = Double.parseDouble(args[2]);
                    }
                    catch (Exception ex2) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    profile4.setVerticalLimit(verticalLimit);
                    SpigotConfig.saveKnockback();
                    sender.sendMessage(ChatColor.GREEN + "Set " + profile4.getName() + "'s Vertical Limit to " + verticalLimit + ".");
                    return true;
                }
                case "setextrahorizontal": {
                    if (args.length != 3) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    Knockback profile4 = SpigotConfig.getKnockbackByName(args[1]);
                    if (profile4 == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }
                    double extraHorizontal;
                    try {
                        extraHorizontal = Double.parseDouble(args[2]);
                    }
                    catch (Exception ex3) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    profile4.setExtraHorizontal(extraHorizontal);
                    SpigotConfig.saveKnockback();
                    sender.sendMessage(ChatColor.GREEN + "Set " + profile4.getName() + "'s Extra Horizontal to " + extraHorizontal + ".");
                    return true;
                }
                case "sethorizontal": {
                    if (args.length != 3) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    Knockback profile4 = SpigotConfig.getKnockbackByName(args[1]);
                    if (profile4 == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }
                    double horizonal;
                    try {
                        horizonal = Double.parseDouble(args[2]);
                    }
                    catch (Exception ex4) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    profile4.setHorizontal(horizonal);
                    SpigotConfig.saveKnockback();
                    sender.sendMessage(ChatColor.GREEN + "Set " + profile4.getName() + "'s Horizontal to " + horizonal + ".");
                    return true;
                }
                case "setvertical": {
                    if (args.length != 3) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    Knockback profile4 = SpigotConfig.getKnockbackByName(args[1]);
                    if (profile4 == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }
                    double vertical;
                    try {
                        vertical = Double.parseDouble(args[2]);
                    }
                    catch (Exception ex5) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    profile4.setVertical(vertical);
                    SpigotConfig.saveKnockback();
                    sender.sendMessage(ChatColor.GREEN + "Set " + profile4.getName() + "'s Vertical to " + vertical + ".");
                    return true;
                }
                case "list": {
                    SpigotConfig.sendKnockbackInfo(sender);
                    return true;
                }
                case "setfriction": {
                    if (args.length != 3) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    Knockback profile4 = SpigotConfig.getKnockbackByName(args[1]);
                    if (profile4 == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }
                    double friction;
                    try {
                        friction = Double.parseDouble(args[2]);
                    }
                    catch (Exception ex6) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    profile4.setFriction(friction);
                    SpigotConfig.saveKnockback();
                    sender.sendMessage(ChatColor.GREEN + "Set " + profile4.getName() + "'s Friction to " + friction + ".");
                    return true;
                }
                case "setglobal": {
                    if (args.length <= 1) {
                        break;
                    }
                    Knockback profile4 = SpigotConfig.getKnockbackByName(args[1]);
                    if (profile4 == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }
                    SpigotConfig.globalKbProfile = profile4;
                    SpigotConfig.saveKnockback();
                    sender.sendMessage(ChatColor.GREEN + "Global profile set to " + profile4.getName() + ".");
                    return true;
                }
                case "kohi": {
                    if (args.length != 3) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    Knockback profile4 = SpigotConfig.getKnockbackByName(args[1]);
                    if (profile4 == null) {
                        sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                        return true;
                    }
                    boolean enabled;
                    try {
                        enabled = Boolean.parseBoolean(args[2]);
                    }
                    catch (Exception ex7) {
                        sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                        return true;
                    }
                    profile4.setKohi(enabled);
                    SpigotConfig.saveKnockback();
                    sender.sendMessage(ChatColor.GREEN + "Set " + profile4.getName() + "'s Kohi to " + enabled + ".");
                    return true;
                }
                case "autowtap": {
                    break;
                }
                default:
                    break Label_2302;
            }
            if (args.length != 3) {
                sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                return true;
            }
            Knockback profile4 = SpigotConfig.getKnockbackByName(args[1]);
            if (profile4 == null) {
                sender.sendMessage(ChatColor.RED + "A profile with that name couldn't be found.");
                return true;
            }
            boolean enabled;
            try {
                enabled = Boolean.parseBoolean(args[2]);
            }
            catch (Exception ex7) {
                sender.sendMessage(ChatColor.RED + "Wrong syntax.");
                return true;
            }
            profile4.setWTap(enabled);
            SpigotConfig.saveKnockback();
            sender.sendMessage(ChatColor.GREEN + "Set " + profile4.getName() + "'s Auto WTap to " + enabled + ".");
            return true;
        }
        sender.sendMessage(this.usageMessage);
        return true;
    }
}