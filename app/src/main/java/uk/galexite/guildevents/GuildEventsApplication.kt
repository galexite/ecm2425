package uk.galexite.guildevents

import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uk.galexite.guildevents.data.EventDatabase
import uk.galexite.guildevents.data.repository.EventRepository
import uk.galexite.guildevents.network.updateDatabaseTask
import java.util.*

class GuildEventsApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { EventDatabase.getDatabase(this) }
    val eventRepository by lazy { EventRepository(database.organisationDao(), database.eventDao()) }

    fun updateDatabase() {
        val sharedPreferences = getSharedPreferences(
            getString(R.string.preference_file_key), MODE_PRIVATE
        )

        val lastUpdated = Date(
            sharedPreferences.getLong(
                getString(R.string.preference_last_updated), 0
            )
        )

        applicationScope.launch {
            val updated = updateDatabaseTask(lastUpdated, eventRepository)

            Log.d(TAG, "updateDatabase: updateDatabaseTask finished!")
            if (updated != null) {
                // Update our last modified time
                sharedPreferences.edit()
                    .putLong(getString(R.string.preference_last_updated), updated.time)
                    .apply()
                Log.d(TAG, "updateDatabase: successfully updated database: $updated")
            }
        }
    }

    companion object {
        private const val TAG = "GuildEventsApplication"
    }
}