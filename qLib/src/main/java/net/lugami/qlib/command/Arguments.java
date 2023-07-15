package net.lugami.qlib.command;

import java.util.List;
import java.util.Set;

public class Arguments {
    private final Set<String> flags;
    private final List<String> arguments;

    public Arguments(List<String> arguments, Set<String> flags) {
        this.arguments = arguments;
        this.flags = flags;
    }

    public boolean hasFlag(String flag) {
        return this.flags.contains(flag.toLowerCase());
    }

    public String join(int from, int to, char delimiter) {
        if (to > this.arguments.size() - 1 || to < 1) {
            to = this.arguments.size() - 1;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = from; i <= to; ++i) {
            builder.append(this.arguments.get(i));
            if (i == to) continue;
            builder.append(delimiter);
        }
        return builder.toString();
    }

    public String join(int from, char delimiter) {
        return this.join(from, -1, delimiter);
    }

    public String join(int from) {
        return this.join(from, ' ');
    }

    public String join(char delimiter) {
        return this.join(0, delimiter);
    }

    public String join() {
        return this.join(' ');
    }

    public Set<String> getFlags() {
        return this.flags;
    }

    public List<String> getArguments() {
        return this.arguments;
    }
}

