package uk.galexite.guildevents.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.galexite.guildevents.data.entity.Event

@Dao
interface EventDao {
    /**
     * Add a new [Event] to the database.
     * @param event the [Event] to add
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    /**
     * Add multiple new [Event]s to the database.
     * @param events several [Event]s to add
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg events: Event)

    /**
     * Get all [Event] objects stored in the database which have a start date in the future.
     * @return a [Flow] container for the list of [Event] objects
     */
    @get:Query("SELECT * from event WHERE fromDate > CURRENT_TIMESTAMP ORDER BY fromDate ASC")
    val allEventsFromNow: Flow<List<Event>>

    /**
     * Get all [Event]s organised by the Organisation matching the given organiserId.
     *
     * @param organiserId the Organiser's id to filter events by.
     * @return a [Flow] container for the list of [Event] objects
     */
    @Query("SELECT * from event WHERE organiserId = :organiserId AND fromDate > CURRENT_TIMESTAMP ORDER BY fromDate ASC")
    fun getAllEventsOrganisedBy(organiserId: Int): Flow<List<Event>>

    /**
     * Get a specific [Event] stored in the database given its id.
     * @param id the unique identifier for the [Event] to retrieve
     * @return a {@Link Flow} container for the [Event]
     */
    @Query("SELECT * from event WHERE id = :id LIMIT 1")
    fun getEvent(id: Int): Flow<Event>
}