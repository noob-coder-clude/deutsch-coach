package de.michelcoach;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public final class Store {
    private static final String NAME = "deutsch_coach";
    private static Store inst;
    private final SharedPreferences sp;

    private Store(Context c) { sp = c.getApplicationContext().getSharedPreferences(NAME, 0); }

    public static synchronized Store get(Context c) {
        if (inst == null) inst = new Store(c);
        return inst;
    }

    public int coachLesson() { return sp.getInt("coach_lesson", startLesson()); }
    public void setCoachLesson(int v) { sp.edit().putInt("coach_lesson", v).apply(); }

    public int coachIndex() { return sp.getInt("coach_index", 0); }
    public void setCoachIndex(int v) { sp.edit().putInt("coach_index", v).apply(); }

    public int startLesson() { return sp.getInt("start_lesson", 1); }
    public void setStartLesson(int v) { sp.edit().putInt("start_lesson", v).apply(); }

    public boolean hideDe() { return sp.getBoolean("hide_de", false); }
    public void setHideDe(boolean v) { sp.edit().putBoolean("hide_de", v).apply(); }

    public boolean hideFa() { return sp.getBoolean("hide_fa", false); }
    public void setHideFa(boolean v) { sp.edit().putBoolean("hide_fa", v).apply(); }

    public Set<String> doneDays() { return new HashSet<>(sp.getStringSet("days", new HashSet<>())); }

    public void markToday() {
        Set<String> s = new HashSet<>(sp.getStringSet("days", new HashSet<>()));
        s.add(Dates.today());
        sp.edit().putStringSet("days", s).apply();
    }

    public boolean placementDone() { return sp.getBoolean("placement_done", false); }
    public void setPlacementDone(boolean v) { sp.edit().putBoolean("placement_done", v).apply(); }
}
