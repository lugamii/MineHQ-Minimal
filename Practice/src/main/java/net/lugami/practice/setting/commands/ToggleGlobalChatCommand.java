package net.lugami.practice.setting.commands;

import net.lugami.qlib.command.Command;
import net.lugami.practice.Practice;
import net.lugami.practice.setting.Setting;
import net.lugami.practice.setting.SettingHandler;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * /toggleglobalchat commands, allows players to toggle {@link Setting#ENABLE_GLOBAL_CHAT} setting
 */
public final class ToggleGlobalChatCommand {

    @Command(names = {"toggleGlobalChat", "tgc", "togglechat"}, permission = "")
    public static void toggleGlobalChat(Player sender) {
        if (!Setting.ENABLE_GLOBAL_CHAT.canUpdate(sender)) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        SettingHandler settingHandler = Practice.getInstance().getSettingHandler();
        boolean enabled = !settingHandler.getSetting(sender, Setting.ENABLE_GLOBAL_CHAT);

        settingHandler.updateSetting(sender, Setting.ENABLE_GLOBAL_CHAT, enabled);

        if (enabled) {
            sender.sendMessage(ChatColor.GREEN + "Toggled global chat on.");
        } else {
            sender.sendMessage(ChatColor.RED + "Toggled global chat off.");
        }
    }

}