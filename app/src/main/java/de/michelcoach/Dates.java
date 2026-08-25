package de.michelcoach;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class Dates {

    private static final long[] INTERVALS_DAYS = {1, 3, 7, 14};

    public static String today() {
        Calendar c = Calendar.getInstance();
        return String.format(java.util.Locale.US, "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    public static int streak(Set<String> days) {
        if (days.isEmpty()) return 0;
        Calendar c = Calendar.getInstance();
        if (!days.contains(fmt(c))) {
            c.add(Calendar.DAY_OF_YEAR, -1);
            if (!days.contains(fmt(c))) return 0;
        }
        int streak = 0;
        while (days.contains(fmt(c))) {
            streak++;
            c.add(Calendar.DAY_OF_YEAR, -1);
        }
        return streak;
    }

    public static long dueFromBox(int box) {
        int idx = Math.max(0, Math.min(box, INTERVALS_DAYS.length)) - 1;
        if (idx < 0) return System.currentTimeMillis();
        return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(INTERVALS_DAYS[idx]);
    }

    private static String fmt(Calendar c) {
        return String.format(java.util.Locale.US, "%04d-%02d-%02d",
                c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    private Dates() {}
}
