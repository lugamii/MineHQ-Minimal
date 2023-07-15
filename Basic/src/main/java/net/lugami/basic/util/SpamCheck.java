//package net.lugami.basic.util;
//
//import net.lugami.basic.Basic;
//import net.lugami.qlib.util.TimeUtils;
//import java.beans.ConstructorProperties;
//import java.util.ArrayList;
//import java.util.Hashtable;
//import java.util.List;
//import java.util.UUID;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.entity.Player;
//import org.bukkit.event.player.AsyncPlayerChatEvent;
//
//public class SpamCheck {
//
//    private static Hashtable<String, SpamCheck> storage = new Hashtable();
//    private List<ChatMessage> messages = new ArrayList<>();
//    private String playerName;
//    private int warnings = 10;
//    private long silenceExpires = 0L;
//
//    public static void chat(AsyncPlayerChatEvent event) {
//        String playerName = event.getPlayer().getName();
//        SpamCheck check = storage.get(playerName);
//        if (check == null) {
//            check = new SpamCheck(playerName);
//            storage.put(playerName, check);
//        }
//        check.addMessage(event);
//    }
//
//    private SpamCheck(String name) {
//        this.playerName = name;
//    }
//
//    private void addMessage(AsyncPlayerChatEvent event) {
//        Player player = event.getPlayer();
//        ChatMessage message = new ChatMessage(player.getUniqueId(), System.currentTimeMillis(), event.getMessage());
//        this.messages.add(0, message);
//        if (this.isSilenced()) {
//            event.setCancelled(true);
//            if (this.warnings < 0) {
//                Bukkit.getScheduler().runTask(Basic.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ("mute " + this.playerName + " -s 1h Spam")));
//                return;
//            }
//            player.sendMessage(ChatColor.RED + "You are currently muted. " + this.timeRemaining() + " remaining.");
//            if (this.warnings != 0) {
//                player.sendMessage(ChatColor.RED + "If you continue to use public chat, your mute will be extended.");
//                player.sendMessage(ChatColor.RED + "You have " + this.warnings + " warning" + (this.warnings != 1 ? "s" : "") + ".");
//            } else {
//                player.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + " ! THE NEXT MESSAGE YOU SEND WILL GET YOU MUTED ! ");
//            }
//            --this.warnings;
//        } else {
//            this.warnings = 10;
//            this.checkSpamming(player);
//        }
//        if (this.messages.size() > 10) {
//            this.messages.remove(this.messages.size() - 1);
//        }
//    }
//
//    private void checkSpamming(Player player) {
//        boolean isSpamming = false;
//        if (this.messages.size() >= 3) {
//            String msg = this.messages.get(0).getMessage();
//            boolean same = true;
//            for (int i = 1; i < 3; ++i) {
//                same = same && msg.equals(this.messages.get(i).getMessage());
//            }
//            long time = this.messages.get(0).getWhen() - this.messages.get(2).getWhen();
//            boolean bl = isSpamming = time < 1550L || time < 5000L && same;
//        }
//        if (isSpamming) {
//            this.silence(player, 60);
//            --this.warnings;
//        }
//    }
//
//    private void silence(Player player, int seconds) {
//        this.silenceExpires = System.currentTimeMillis() + (long)(seconds * 1000);
//        player.sendMessage(ChatColor.RED + "You are muted for " + seconds + " seconds");
//        player.sendMessage(ChatColor.RED + "If you still spam, you will be muted!");
//    }
//
//    private String timeRemaining() {
//        int left = (int)(this.silenceExpires - System.currentTimeMillis()) / 1000;
//        return TimeUtils.formatIntoDetailedString((int)left);
//    }
//
//    private boolean isSilenced() {
//        return this.silenceExpires > System.currentTimeMillis();
//    }
//
//    public void clear() {
//        this.messages.clear();
//    }
//
//    private static class ChatMessage {
//        private UUID uuid;
//        private long when;
//        private String message;
//
//        public UUID getUuid() {
//            return this.uuid;
//        }
//
//        public long getWhen() {
//            return this.when;
//        }
//
//        public String getMessage() {
//            return this.message;
//        }
//
//        public void setUuid(UUID uuid) {
//            this.uuid = uuid;
//        }
//
//        public void setWhen(long when) {
//            this.when = when;
//        }
//
//        public void setMessage(String message) {
//            this.message = message;
//        }
//
//        public boolean equals(Object o) {
//            if (o == this) {
//                return true;
//            }
//            if (!(o instanceof ChatMessage)) {
//                return false;
//            }
//            ChatMessage other = (ChatMessage)o;
//            if (!other.canEqual(this)) {
//                return false;
//            }
//            UUID this$uuid = this.getUuid();
//            UUID other$uuid = other.getUuid();
//            if (!(this$uuid == null ? other$uuid == null : (this$uuid).equals(other$uuid))) {
//                return false;
//            }
//            if (this.getWhen() != other.getWhen()) {
//                return false;
//            }
//            String this$message = this.getMessage();
//            String other$message = other.getMessage();
//            return this$message == null ? other$message == null : this$message.equals(other$message);
//        }
//
//        protected boolean canEqual(Object other) {
//            return other instanceof ChatMessage;
//        }
//
//        public int hashCode() {
//            int PRIME = 59;
//            int result = 1;
//            UUID $uuid = this.getUuid();
//            result = result * 59 + ($uuid == null ? 43 : ($uuid).hashCode());
//            long $when = this.getWhen();
//            result = result * 59 + (int)($when >>> 32 ^ $when);
//            String $message = this.getMessage();
//            result = result * 59 + ($message == null ? 43 : $message.hashCode());
//            return result;
//        }
//
//        public String toString() {
//            return "SpamCheck.ChatMessage(uuid=" + this.getUuid() + ", when=" + this.getWhen() + ", message=" + this.getMessage() + ")";
//        }
//
//        @ConstructorProperties(value={"uuid", "when", "message"})
//        public ChatMessage(UUID uuid, long when, String message) {
//            this.uuid = uuid;
//            this.when = when;
//            this.message = message;
//        }
//    }
//}
//
