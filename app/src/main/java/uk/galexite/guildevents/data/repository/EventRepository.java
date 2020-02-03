package uk.galexite.guildevents.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import uk.galexite.guildevents.data.EventDatabase;
import uk.galexite.guildevents.data.dao.EventDao;
import uk.galexite.guildevents.data.entity.Event;

public class EventRepository {

    private final EventDao mEventDao;
    private final LiveData<List<Event>> mEvents;

    public EventRepository(Application application) {
        EventDatabase database = EventDatabase.getDatabase(application);
        mEventDao = database.eventDao();
        mEvents = mEventDao.getAllEvents();
    }

    public LiveData<List<Event>> getAllEvents() {
        return mEvents;
    }

    public LiveData<Event> getEvent(int id) {
        return mEventDao.getEvent(id);
    }
}
