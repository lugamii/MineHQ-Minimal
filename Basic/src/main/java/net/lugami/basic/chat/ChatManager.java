package net.lugami.basic.chat;

public class ChatManager {

    public static final int SLOW_CHAT_SECONDS = 15;
    private boolean muted;
    private boolean slowed;

    public boolean isMuted() {
        return this.muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isSlowed() {
        return this.slowed;
    }

    public void setSlowed(boolean slowed) {
        this.slowed = slowed;
    }
}

