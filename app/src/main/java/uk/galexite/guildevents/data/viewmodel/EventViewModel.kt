package uk.galexite.guildevents.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import uk.galexite.guildevents.data.entity.Event;
import uk.galexite.guildevents.data.entity.Organisation;
import uk.galexite.guildevents.data.repository.EventRepository;

/**
 * An abstraction which sits between the View and the {@link EventRepository} and handles what data
 * is accessible from the View.
 *
 * All data comes from the Room repository in a {@link LiveData} container which allows the view to
 * observe the data being retrieved from the data in a separate thread from the user interface and
 * automatically update the interface when the data is changed.
 */
public class EventViewModel extends AndroidViewModel {

    private final EventRepository mRepository;
    private final LiveData<List<Organisation>> mOrganisations;
    private final LiveData<List<Event>> mEvents;

    public EventViewModel(@NonNull Application application) {
        super(application);

        mRepository = new EventRepository(application);

        mOrganisations = mRepository.getAllOrganisations();
        mEvents = mRepository.getAllEventsFromNow();
    }

    /**
     * Get the repository associated with this view model.
     *
     * @return the {@link EventRepository}
     */
    public EventRepository getRepository() {
        return mRepository;
    }

    /**
     * Get all Organisations from the repository. See class JavaDoc for information on LiveData.
     * @return a {@link LiveData} container for the list of Organisation objects.
     */
    public LiveData<List<Organisation>> getAllOrganisations() {
        return mOrganisations;
    }

    /**
     * Get all Events from the repository.
     * @return a {@link LiveData} container for the list of Event objects
     */
    public LiveData<List<Event>> getAllEventsFromNow() {
        return mEvents;
    }

    /**
     * Get all Events from the repository organised by the Organiser given by the id.
     * @param organiserId the Organiser's id to filter the Events by
     * @return a {@link LiveData} container for the list of Event objects
     */
    public LiveData<List<Event>> getAllEventsOrganisedBy(int organiserId) {
        return mRepository.getAllEventsOrganisedBy(organiserId);
    }

    /**
     * Get a specific Event given its id from the repository.
     * @param id the unique identifier for the Event
     * @return a {@link LiveData} container for the Event
     */
    public LiveData<Event> getEvent(int id) {
        return mRepository.getEvent(id);
    }
}
