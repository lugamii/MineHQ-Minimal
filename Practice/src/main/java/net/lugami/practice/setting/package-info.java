/**
 * Handles accessing, saving, updating, and presentation of player settings.
 *
 * This includes the /settings commands, a settings menu, persistence, etc.
 * Clients using the settings API should only concern themselves with {@link net.lugami.practice.setting.event.SettingUpdateEvent},
 * {@link net.lugami.practice.setting.SettingHandler#getSetting(org.bukkit.entity.Player, Setting)} (java.util.UUID, Setting)} and
 * {@link net.lugami.practice.setting.SettingHandler#updateSetting(org.bukkit.entity.Player, Setting, boolean)},
 */
package net.lugami.practice.setting;