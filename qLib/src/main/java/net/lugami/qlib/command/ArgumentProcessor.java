package net.lugami.qlib.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;

public class ArgumentProcessor
implements Processor<String[], Arguments> {
    @Override
    public Arguments process(String[] value) {
        HashSet<String> flags = new HashSet<>();
        ArrayList<String> arguments = new ArrayList<>();
        for (String s : value) {
            if (s.isEmpty()) continue;
            if (s.charAt(0) == '-' && !s.equals("-") && this.matches(s)) {
                String flag = this.getFlagName(s);
                flags.add(flag);
                continue;
            }
            arguments.add(s);
        }
        return new Arguments(arguments, flags);
    }

    private String getFlagName(String flag) {
        Matcher matcher = Flag.FLAG_PATTERN.matcher(flag);
        if (matcher.matches()) {
            String name = matcher.replaceAll("$2$3");
            return name.length() == 1 ? name : name.toLowerCase();
        }
        return null;
    }

    private boolean matches(String flag) {
        return Flag.FLAG_PATTERN.matcher(flag).matches();
    }
}

