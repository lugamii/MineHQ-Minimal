package net.lugami.qlib.command.parameter.filter;

import java.util.regex.Pattern;

public class NormalFilter
extends BaseFilter {
    public NormalFilter() {
        this.bannedPatterns.add(Pattern.compile("n+[i1l|]+gg+[e3]+r+", Pattern.CASE_INSENSITIVE));
        this.bannedPatterns.add(Pattern.compile("k+i+l+l+ *y*o*u+r+ *s+e+l+f+", Pattern.CASE_INSENSITIVE));
        this.bannedPatterns.add(Pattern.compile("f+a+g+[o0]+t+", Pattern.CASE_INSENSITIVE));
        this.bannedPatterns.add(Pattern.compile("\\bk+y+s+\\b", Pattern.CASE_INSENSITIVE));
        this.bannedPatterns.add(Pattern.compile("b+e+a+n+e+r+", Pattern.CASE_INSENSITIVE));
        this.bannedPatterns.add(Pattern.compile("\\d{1,3}[,.]\\d{1,3}[,.]\\d{1,3}[,.]\\d{1,3}", Pattern.CASE_INSENSITIVE));
        this.bannedPatterns.add(Pattern.compile("optifine\\.(?=\\w+)(?!net)", Pattern.CASE_INSENSITIVE));
        this.bannedPatterns.add(Pattern.compile("gyazo\\.(?=\\w+)(?!com)", Pattern.CASE_INSENSITIVE));
        this.bannedPatterns.add(Pattern.compile("prntscr\\.(?=\\w+)(?!com)", Pattern.CASE_INSENSITIVE));
    }
}

