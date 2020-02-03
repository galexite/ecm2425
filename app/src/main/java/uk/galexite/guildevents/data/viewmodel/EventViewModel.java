package uk.galexite.guildevents.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import uk.galexite.guildevents.data.entity.Event;
import uk.galexite.guildevents.data.repository.EventRepository;

public class EventViewModel extends AndroidViewModel {

    private final EventRepository mRepository;

    private final LiveData<List<Event>> mEvents;

    public EventViewModel(@NonNull Application application) {
        super(application);

        mRepository = new EventRepository(application);
        mEvents = mRepository.getAllEvents();
    }

    public LiveData<List<Event>> getAllEvents() {
        return mEvents;
    }

    public LiveData<Event> getEvent(int id) {
        return mRepository.getEvent(id);
    }
}
