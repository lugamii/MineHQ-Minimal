package com.lunarclient.bukkitapi.object;

import com.google.common.base.Preconditions;
import lombok.Getter;

import java.time.Duration;

public final class LCNotification {

    @Getter private final String message;
    @Getter private final long durationMs;
    @Getter private final Level level;

    public LCNotification(String message, Duration duration) {
        this(message, duration, Level.INFO);
    }

    public LCNotification(String message, Duration duration, Level level) {
        this.message = Preconditions.checkNotNull(message, "message");
        this.durationMs = duration.toMillis();
        this.level = Preconditions.checkNotNull(level, "level");
    }

    public enum Level {

        INFO, ERROR

    }

}