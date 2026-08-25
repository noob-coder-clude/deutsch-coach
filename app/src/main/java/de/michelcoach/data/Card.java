package de.michelcoach.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "cards")
public class Card {
    @PrimaryKey
    public String id;
    public int box;
    public long dueAt;
    public int seen;
    public int lapses;

    public Card() {}

    @Ignore
    public Card(String id) {
        this.id = id;
        this.box = 0;
        this.dueAt = System.currentTimeMillis();
        this.seen = 0;
        this.lapses = 0;
    }
}
