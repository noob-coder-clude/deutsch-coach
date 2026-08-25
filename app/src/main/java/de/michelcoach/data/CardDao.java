package de.michelcoach.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CardDao {

    @Query("SELECT * FROM cards WHERE id = :id")
    Card get(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Card c);

    @Query("SELECT * FROM cards WHERE box > 0 AND dueAt <= :now ORDER BY dueAt ASC LIMIT :limit")
    List<Card> due(long now, int limit);

    @Query("SELECT COUNT(*) FROM cards WHERE box >= 4")
    int masteredCount();

    @Query("SELECT COUNT(*) FROM cards WHERE seen > 0")
    int seenCount();

    @Query("SELECT COUNT(*) FROM cards")
    int trackedCount();

    @Query("SELECT COALESCE(SUM(seen), 0) FROM cards")
    int totalReviews();

    @Query("SELECT COUNT(*) FROM cards WHERE id LIKE :lessonPrefix || '_%'")
    int startedCount(String lessonPrefix);

    @Query("SELECT COUNT(*) FROM cards WHERE id LIKE :lessonPrefix || '_%' AND box >= 4")
    int masteredInLesson(String lessonPrefix);
}
