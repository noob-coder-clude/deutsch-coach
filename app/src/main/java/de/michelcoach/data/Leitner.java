package de.michelcoach.data;

import de.michelcoach.Dates;

public final class Leitner {

    public static class Result {
        public final int box;
        public final long dueAt;

        Result(int box, long dueAt) { this.box = box; this.dueAt = dueAt; }
    }

    /** box: 0 = learning, 1..4 = scheduled with 1/3/7/14 day gaps, 4 = mastered. */
    public static Result answer(Card c, boolean easy) {
        if (easy) {
            c.box = Math.min(4, Math.max(1, c.box) + 1);
            c.dueAt = Dates.dueFromBox(c.box);
        } else {
            c.lapses++;
            c.box = Math.max(0, c.box - 1);
            c.dueAt = System.currentTimeMillis();
        }
        c.seen++;
        return new Result(c.box, c.dueAt);
    }

    private Leitner() {}
}
