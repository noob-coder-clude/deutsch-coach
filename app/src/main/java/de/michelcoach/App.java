package de.michelcoach;

import android.app.Application;

import androidx.room.Room;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.michelcoach.data.AppDb;
import de.michelcoach.data.Course;

public class App extends Application {

    private static AppDb db;
    private static Course course;
    private static ExecutorService io;

    @Override
    public void onCreate() {
        super.onCreate();
        db = Room.databaseBuilder(getApplicationContext(), AppDb.class, "deutsch.db")
                .fallbackToDestructiveMigration()
                .build();
        course = new Course(this);
        io = Executors.newSingleThreadExecutor();
    }

    public static AppDb db() { return db; }

    public static Course course() { return course; }

    public static ExecutorService io() { return io; }
}
