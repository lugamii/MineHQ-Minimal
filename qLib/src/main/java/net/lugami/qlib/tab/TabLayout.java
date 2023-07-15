package net.lugami.qlib.tab;

import net.lugami.qlib.qLib;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TabLayout {
    private static final AtomicReference<Object> TAB_LAYOUT_1_8 = new AtomicReference<>();
    private static final AtomicReference<Object> TAB_LAYOUT_DEFAULT = new AtomicReference<>();
    private static final String[] ZERO_VALUE_STRING = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    private static final String[] ZERO_VALUE_STRING_18 = new String[]{"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""};
    private static final Map<String, TabLayout> tabLayouts = new HashMap<>();
    protected static int WIDTH = 3;
    protected static int HEIGHT = 20;
    protected static final String EMPTY_TAB_HEADERFOOTER = "{\"translate\":\"\"}";
    private static final List<String> emptyStrings = new ArrayList<>();
    private String[] tabNames;
    private int[] tabPings;
    private final boolean is18;
    private String header = "{\"translate\":\"\"}";
    private String footer = "{\"translate\":\"\"}";

    private TabLayout(boolean is18) {
        this(is18, false);
    }

    private TabLayout(boolean is18, boolean fill) {
        this.is18 = is18;
        this.tabNames = is18 ? ZERO_VALUE_STRING_18.clone() : ZERO_VALUE_STRING.clone();
        int[] arrn = this.tabPings = is18 ? new int[WIDTH * HEIGHT + 20] : new int[WIDTH * HEIGHT];
        if (fill) {
            for (int i = 0; i < this.tabNames.length; ++i) {
                this.tabNames[i] = TabLayout.genEmpty();
                this.tabPings[i] = -1;
            }
        }
        Arrays.sort(this.tabNames);
    }

    public void set(int x, int y, String name, int ping) {
        if (!this.validate(x, y, true)) {
            return;
        }
        int pos = this.is18 ? y + x * HEIGHT : x + y * WIDTH;
        this.tabNames[pos] = ChatColor.translateAlternateColorCodes('&', name);
        this.tabPings[pos] = ping;
    }

    public void set(int x, int y, String name) {
        this.set(x, y, name, 0);
    }

    public void set(int x, int y, Player player) {
        this.set(x, y, player.getName(), ((CraftPlayer)player).getHandle().ping);
    }

    public String getStringAt(int x, int y) {
        this.validate(x, y);
        int pos = this.is18 ? y + x * HEIGHT : x + y * WIDTH;
        return this.tabNames[pos];
    }

    public int getPingAt(int x, int y) {
        this.validate(x, y);
        int pos = this.is18 ? y + x * HEIGHT : x + y * WIDTH;
        return this.tabPings[pos];
    }

    public boolean validate(int x, int y, boolean silent) {
        if (x >= WIDTH) {
            if (!silent) {
                throw new IllegalArgumentException("x >= WIDTH (" + WIDTH + ")");
            }
            return false;
        }
        if (y >= HEIGHT) {
            if (!silent) {
                throw new IllegalArgumentException("y >= HEIGHT (" + HEIGHT + ")");
            }
            return false;
        }
        return true;
    }

    public boolean validate(int x, int y) {
        return this.validate(x, y, false);
    }

    public static String genEmpty() {
        String colorChars = "abcdefghijpqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; ++i) {
            builder.append('\u00a7').append(colorChars.charAt(qLib.RANDOM.nextInt(colorChars.length())));
        }
        String s = builder.toString();
        if (emptyStrings.contains(s)) {
            return TabLayout.genEmpty();
        }
        emptyStrings.add(s);
        return s;
    }

    protected String[] getTabNames() {
        return this.tabNames;
    }

    protected int[] getTabPings() {
        return this.tabPings;
    }

    public boolean is18() {
        return this.is18;
    }

    public void setHeader(String header) {
        this.header = ComponentSerializer.toString(new TextComponent(ChatColor.translateAlternateColorCodes('&', header)));
    }

    public void setFooter(String footer) {
        this.footer = ComponentSerializer.toString(new TextComponent(ChatColor.translateAlternateColorCodes('&', footer)));
    }

    public void reset() {
        this.tabNames = this.is18 ? ZERO_VALUE_STRING_18.clone() : ZERO_VALUE_STRING.clone();
        this.tabPings = this.is18 ? new int[WIDTH * HEIGHT + 20] : new int[WIDTH * HEIGHT];
    }

    public static TabLayout create(Player player) {
        if (tabLayouts.containsKey(player.getName())) {
            TabLayout layout = tabLayouts.get(player.getName());
            layout.reset();
            return layout;
        }
        tabLayouts.put(player.getName(), new TabLayout(TabUtils.is18(player)));
        return tabLayouts.get(player.getName());
    }

    protected static void remove(Player player) {
        tabLayouts.remove(player.getName());
    }

    public static TabLayout createEmpty(Player player) {
        if (TabUtils.is18(player)) {
            return TabLayout.getTAB_LAYOUT_1_8();
        }
        return TabLayout.getTAB_LAYOUT_DEFAULT();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static TabLayout getTAB_LAYOUT_1_8() {
        Object value = TAB_LAYOUT_1_8.get();
        if (value == null) {
            synchronized (TAB_LAYOUT_1_8) {
                value = TAB_LAYOUT_1_8.get();
                if (value == null) {
                    TabLayout actualValue = new TabLayout(true, true);
                    value = actualValue == null ? TAB_LAYOUT_1_8 : actualValue;
                    TAB_LAYOUT_1_8.set(value);
                }
            }
        }
        return (TabLayout)(value == TAB_LAYOUT_1_8 ? null : value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static TabLayout getTAB_LAYOUT_DEFAULT() {
        Object value = TAB_LAYOUT_DEFAULT.get();
        if (value == null) {
            synchronized (TAB_LAYOUT_DEFAULT) {
                value = TAB_LAYOUT_DEFAULT.get();
                if (value == null) {
                    TabLayout actualValue = new TabLayout(false, true);
                    value = actualValue == null ? TAB_LAYOUT_DEFAULT : actualValue;
                    TAB_LAYOUT_DEFAULT.set(value);
                }
            }
        }
        return (TabLayout)(value == TAB_LAYOUT_DEFAULT ? null : value);
    }

    protected String getHeader() {
        return this.header;
    }

    protected String getFooter() {
        return this.footer;
    }
}

