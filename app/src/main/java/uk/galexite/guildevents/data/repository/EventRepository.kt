package uk.galexite.guildevents.data.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import uk.galexite.guildevents.data.dao.EventDao
import uk.galexite.guildevents.data.dao.OrganisationDao
import uk.galexite.guildevents.data.entity.Event
import uk.galexite.guildevents.data.entity.Organisation

/**
 * A class that combines data sourced from the Internet with those cached locally in the database.
 */
class EventRepository(
    private val organisationDao: OrganisationDao,
    private val eventDao: EventDao
) {

    /**
     * Retrieves all the organisations stored in the database.
     *
     * @return a [Flow] container of the list of [Organisation] objects
     */
    val allOrganisations: Flow<List<Organisation>> = organisationDao.allOrganisations

    /**
     * Retrieves all the events stored in the database.
     *
     * @return a [Flow] container for the list of events
     */
    val allEventsFromNow: Flow<List<Event>> = eventDao.allEventsFromNow

    /**
     * Retrieves all the events organised by the [Organisation] with the given id stored in the
     * database.
     *
     * @return a [Flow] container for the list of events
     */
    fun getAllEventsOrganisedBy(organiserId: Int): Flow<List<Event>> =
        eventDao.getAllEventsOrganisedBy(organiserId)

    /**
     * Gets a specific event given an id.
     *
     * @param id the unique identifier for the [Event]
     * @return a [Flow] container
     */
    fun getEvent(id: Int): Flow<Event> = eventDao.getEvent(id)

    /**
     * Inserts a new [Event] in to the database.
     *
     * @param event the [Event] object to insert in to the database
     */
    @WorkerThread
    suspend fun insertEvent(event: Event) {
        eventDao.insert(event)
    }

    /**
     * Inserts a new [Organisation] in to the database.
     *
     * @param organisation the [Organisation] to insert in to the database
     */
    @WorkerThread
    suspend fun insertOrganisation(organisation: Organisation) {
        organisationDao.insert(organisation)
    }

    /**
     * Inserts multiple new [Organisation]s in to the database.
     *
     * @param organisations several [Organisation]s to insert in to the database
     */
    @WorkerThread
    suspend fun insertAllOrganisations(vararg organisations: Organisation) {
        organisationDao.insertAll(*organisations)
    }

    /**
     * Inserts multiple new [Event]s in to the database.
     *
     * @param events several [Event]s to insert in to the database
     */
    @WorkerThread
    suspend fun insertAllEvents(vararg events: Event) {
        eventDao.insertAll(*events)
    }
}