package uk.galexite.guildevents.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.galexite.guildevents.data.entity.Organisation

@Dao
interface OrganisationDao {
    /**
     * Add a new [Organisation] to the database.
     * @param organisation the [Organisation] to add
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(organisation: Organisation)

    /**
     * Add multiple [Organisation]s to the database.
     * @param organisation the [Organisation] to add
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg organisations: Organisation)

    /**
     * Gets all the [Organisation]s stored in the database.
     *
     * @return a [Flow] container containing a list of all the [Organisation] objects
     */
    @get:Query("SELECT * FROM organisation")
    val allOrganisations: Flow<List<Organisation>>
}