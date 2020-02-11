package uk.galexite.guildevents.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import uk.galexite.guildevents.data.entity.Event;
import uk.galexite.guildevents.data.repository.EventRepository;

/**
 * An abstraction which sits between the View and the {@link EventRepository} and handles what data
 * is accessible from the View.
 */
public class EventViewModel extends AndroidViewModel {

    private final EventRepository mRepository;
    private final LiveData<List<Event>> mEvents;

    public EventViewModel(@NonNull Application application) {
        super(application);

        mRepository = new EventRepository(application);
        mEvents = mRepository.getAllEvents();
    }

    /**
     * Get all Events from the repository.
     * @return a {@link LiveData} container for the list of Event objects
     */
    public LiveData<List<Event>> getAllEvents() {
        return mEvents;
    }

    /**
     * Get a specific Event given its id from the repository.
     * @param id the unique identifier for the Event
     * @return a {@link LiveData} container for the Event
     */
    public LiveData<Event> getEvent(int id) {
        return mRepository.getEvent(id);
    }

    /**
     * Inserts a new Event in to the repository.
     *
     * @param event the Event object to insert in to the repository
     */
    public void insertEvent(final Event event) {
        mRepository.insertEvent(event);
    }
}
