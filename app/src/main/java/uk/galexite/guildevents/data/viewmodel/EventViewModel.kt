package uk.galexite.guildevents.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import uk.galexite.guildevents.data.entity.Event
import uk.galexite.guildevents.data.entity.Organisation
import uk.galexite.guildevents.data.repository.EventRepository

/**
 * An abstraction which sits between the View and the [EventRepository] and handles what data
 * is accessible from the View.
 *
 * All data comes from the Room repository in a [Flow] container which allows the view to
 * observe the data being retrieved from the data in a separate thread from the user interface and
 * automatically update the interface when the data is changed.
 */
class EventViewModel(private val repository: EventRepository) : ViewModel() {
    /**
     * Get all Organisations from the repository. See class JavaDoc for information on LiveData.
     * @return a [Flow] container for the list of Organisation objects.
     */
    val allOrganisations = repository.allOrganisations.asLiveData()

    /**
     * Get all Events from the repository.
     * @return a [Flow] container for the list of Event objects
     */
    val allEventsFromNow = repository.allEventsFromNow.asLiveData()

    /**
     * Get all Events from the repository organised by the Organiser given by the id.
     * @param organiserId the Organiser's id to filter the Events by
     * @return a [Flow] container for the list of Event objects
     */
    fun getAllEventsOrganisedBy(organiserId: Int) =
        repository.getAllEventsOrganisedBy(organiserId).asLiveData()

    /**
     * Get a specific Event given its id from the repository.
     * @param id the unique identifier for the Event
     * @return a [Flow] container for the Event
     */
    fun getEvent(id: Int) = repository.getEvent(id).asLiveData()

    fun insertEvent(event: Event) = viewModelScope.launch {
        repository.insertEvent(event)
    }

    fun insertOrganisation(organisation: Organisation) = viewModelScope.launch {
        repository.insertOrganisation(organisation)
    }
}

class EventViewModelFactory(private val repository: EventRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
