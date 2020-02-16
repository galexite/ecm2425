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
     * @param event the Event to add
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Event event);

    /**
     * Get all Event objects stored in the database which have a start date in the future.
     * @return a {@link LiveData} container for the list of Event objects
     */
    @Query("SELECT * from event WHERE fromDate > CURRENT_TIMESTAMP ORDER BY fromDate ASC")
    LiveData<List<Event>> getAllEventsFromNow();

    /**
     * Get all Events organised by the Organisation matching the given organiserId.
     *
     * @param organiserId the Organiser's id to filter events by.
     * @return a {@link LiveData} container for the list of Event objects
     */
    @Query("SELECT * from event WHERE organiserId = :organiserId ORDER BY fromDate ASC")
    LiveData<List<Event>> getAllEventsOrganisedBy(int organiserId);

    /**
     * Get a specific Event stored in the database given its id.
     * @param id the unique identifier for the Event to retrieve
     * @return a {@Link LiveData} container for the Event
     */
    @Query("SELECT * from event WHERE id = :id LIMIT 1")
    LiveData<Event> getEvent(int id);
}
