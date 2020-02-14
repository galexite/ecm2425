package uk.galexite.guildevents.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import uk.galexite.guildevents.data.entity.Event;

@Dao
public interface EventDao {

    /**
     * Add a new Event to the database.
     *
     * @param event the Event to add
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

// --Commented out by Inspection START (2020-02-11 21:36):
//    /**
//     * Get all Event objects stored in the database.
//     * @return a {@link LiveData} container for the list of Event objects
//     */
//    @Query("SELECT * from event ORDER BY fromDate ASC")
//    LiveData<List<Event>> getAllEvents();
// --Commented out by Inspection STOP (2020-02-11 21:36)

    /**
     * Get all Event objects stored in the database which have a start date in the future.
     *
     * @return a {@link LiveData} container for the list of Event objects
     */
    @Query("SELECT * from event WHERE fromDate > CURRENT_TIMESTAMP")
    LiveData<List<Event>> getAllEventsFromNow();

    /**
     * Get a specific Event stored in the database given its id.
     * @param id the unique identifier for the Event to retrieve
     * @return a {@Link LiveData} container for the Event
     */
    @Query("SELECT * from event WHERE id = :id LIMIT 1")
    LiveData<Event> getEvent(int id);
}
