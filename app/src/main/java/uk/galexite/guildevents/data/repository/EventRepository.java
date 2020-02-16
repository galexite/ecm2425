package uk.galexite.guildevents.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import uk.galexite.guildevents.data.EventDatabase;
import uk.galexite.guildevents.data.dao.EventDao;
import uk.galexite.guildevents.data.dao.OrganisationDao;
import uk.galexite.guildevents.data.entity.Event;
import uk.galexite.guildevents.data.entity.Organisation;

/**
 * A class that combines data sourced from the Internet with those cached locally in the database.
 */
public class EventRepository {

    private final OrganisationDao mOrganisationDao;
    private final LiveData<List<Organisation>> mOrganisations;

    private final EventDao mEventDao;
    private final LiveData<List<Event>> mEvents;

    public EventRepository(Application application) {
        EventDatabase database = EventDatabase.getDatabase(application);

        mOrganisationDao = database.organisationDao();
        mOrganisations = mOrganisationDao.getAllOrganisations();

        mEventDao = database.eventDao();
        mEvents = mEventDao.getAllEventsFromNow();
    }

    /**
     * Retrieves all the organisations stored in the database.
     *
     * @return a LiveData container of the list of Organisation objects
     */
    public LiveData<List<Organisation>> getAllOrganisations() {
        return mOrganisations;
    }

    /**
     * Retrieves all the events stored in the database.
     *
     * @return a LiveData container for the list of events
     */
    public LiveData<List<Event>> getAllEventsFromNow() {
        return mEvents;
    }

    /**
     * Retrieves all the events organised by the Organisation with the given id stored in the
     * database.
     *
     * @return a LiveData container for the list of events
     */
    public LiveData<List<Event>> getAllEventsOrganisedBy(int organiserId) {
        return mEventDao.getAllEventsOrganisedBy(organiserId);
    }

    /**
     * Gets a specific event given an id.
     *
     * @param id the unique identifier for the event
     * @return a LiveData container
     */
    public LiveData<Event> getEvent(int id) {
        return mEventDao.getEvent(id);
    }

    /**
     * Inserts (synchronously) a new Event in to the database.
     *
     * This must NOT be run from a UI thread, otherwise the app will crash.
     *
     * @param event the Event object to insert in to the database
     */
    public void insertEventSynchronous(Event event) {
        mEventDao.insert(event);
    }

    /**
     * Inserts (synchronously) a new Organisation in to the database.
     *
     * This must NOT be run from a UI thread, otherwise the app will crash.
     *
     * @param organisation the Organisation to insert in to the database
     */
    public void insertOrganisationSynchronous(Organisation organisation) {
        mOrganisationDao.insert(organisation);
    }
}
