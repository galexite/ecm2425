package uk.galexite.guildevents.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import uk.galexite.guildevents.data.entity.Event;

@Dao
public interface EventDao {

    @Insert
    void insert(Event event);

    @Query("SELECT * from event ORDER BY date ASC")
    LiveData<List<Event>> getAllEvents();

    @Query("SELECT * from event WHERE id = :id LIMIT 1")
    LiveData<Event> getEvent(int id);
}
