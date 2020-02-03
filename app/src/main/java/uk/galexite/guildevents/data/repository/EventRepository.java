package uk.galexite.guildevents.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import uk.galexite.guildevents.data.EventDatabase;
import uk.galexite.guildevents.data.dao.EventDao;
import uk.galexite.guildevents.data.entity.Event;

/**
 * A class that combines data sourced from the Internet with those cached locally in the database.
 */
public class EventRepository {

    private final EventDao mEventDao;
    private final LiveData<List<Event>> mEvents;

    public EventRepository(Application application) {
        EventDatabase database = EventDatabase.getDatabase(application);
        mEventDao = database.eventDao();
        mEvents = mEventDao.getAllEvents();
    }

    /**
     * Retrieves all the events stored in the database and stored on the server.
     *
     * @return a LiveData container for the list of events
     */
    public LiveData<List<Event>> getAllEvents() {
        return mEvents;
    }

    /**
     * Gets a specific event given an id.
     * @param id the unique identifier for the event
     * @return a LiveData container
     */
    public LiveData<Event> getEvent(int id) {
        return mEventDao.getEvent(id);
    }
}
