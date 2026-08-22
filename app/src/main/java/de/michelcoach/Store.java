package de.michelcoach;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Tiny local persistence. No network, no course content stored. */
public final class Store {
    private static final String NAME = "dc";
    private final SharedPreferences sp;

    public Store(Context c) { this.sp = c.getSharedPreferences(NAME, 0); }

    public boolean getBool(String k, boolean d) { return sp.getBoolean(k, d); }
    public void putBool(String k, boolean v) { sp.edit().putBoolean(k, v).apply(); }

    public long getLong(String k, long d) { return sp.getLong(k, d); }
    public void putLong(String k, long v) { sp.edit().putLong(k, v).apply(); }

    public String getStr(String k, String d) { return sp.getString(k, d); }
    public void putStr(String k, String v) { sp.edit().putString(k, v).apply(); }

    /** Hard sentences list (user-owned). */
    public List<String> getLines() {
        Set<String> s = sp.getStringSet("lines", new HashSet<>());
        return new ArrayList<>(s);
    }
    public void addLine(String line) {
        Set<String> s = new HashSet<>(sp.getStringSet("lines", new HashSet<>()));
        s.add(line);
        sp.edit().putStringSet("lines", s).apply();
    }
    public void setLines(List<String> lines) {
        sp.edit().putStringSet("lines", new HashSet<>(lines)).apply();
    }

    /** Spaced-repetition timestamps keyed by line hash. */
    public long getNextReview(String key) { return sp.getLong("sr_" + key, 0); }
    public void setNextReview(String key, long t) { sp.edit().putLong("sr_" + key, t).apply(); }

    /** Current phase/lektion the user is on (1..6). */
    public int getPhase() { return sp.getInt("phase", 1); }
    public void setPhase(int p) { sp.edit().putInt("phase", p).apply(); }

    /** Days practiced (date string set). */
    public Set<String> getDoneDays() { return sp.getStringSet("days", new HashSet<>()); }
    public void markDay(String day) {
        Set<String> s = new HashSet<>(sp.getStringSet("days", new HashSet<>()));
        s.add(day);
        sp.edit().putStringSet("days", s).apply();
    }

    public static String key(String line) { return String.valueOf(line.hashCode()); }
}
