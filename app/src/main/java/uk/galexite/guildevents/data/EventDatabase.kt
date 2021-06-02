package uk.galexite.guildevents.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uk.galexite.guildevents.data.dao.EventDao
import uk.galexite.guildevents.data.dao.OrganisationDao
import uk.galexite.guildevents.data.entity.Event
import uk.galexite.guildevents.data.entity.Organisation

/**
 * Singleton database wrapper which is automatically filled in by Room.
 */
@Database(entities = [Event::class, Organisation::class], version = 1, exportSchema = false)
abstract class EventDatabase : RoomDatabase() {
    /**
     * Get a data access object for the Event type.
     *
     * A data access object is an object-oriented facade for accessing the data stored in the
     * database using the SQL query storied in the annotations in the DAO's definition.
     *
     * @return an [EventDao] for this database
     */
    abstract fun eventDao(): EventDao

    /**
     * Get a data access object for the Organisation type.
     *
     * @return an [OrganisationDao] for this database.
     */
    abstract fun organisationDao(): OrganisationDao

    companion object {
        @Volatile
        private var INSTANCE: EventDatabase? = null

        /**
         * Get the singleton instance of EventDatabase and create it if needed.
         *
         * @param context from which the ApplicationContext will be retrieved
         * @return the EventDatabase singleton instance
         */
        fun getDatabase(context: Context): EventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "event_database"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}