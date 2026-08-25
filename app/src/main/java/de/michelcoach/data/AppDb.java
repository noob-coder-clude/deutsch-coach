package de.michelcoach.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Card.class}, version = 1, exportSchema = false)
public abstract class AppDb extends RoomDatabase {
    public abstract CardDao cards();
}
