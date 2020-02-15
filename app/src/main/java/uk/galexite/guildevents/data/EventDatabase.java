package uk.galexite.guildevents.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import uk.galexite.guildevents.data.dao.EventDao;
import uk.galexite.guildevents.data.dao.OrganisationDao;
import uk.galexite.guildevents.data.entity.Event;
import uk.galexite.guildevents.data.entity.Organisation;

/**
 * Singleton database wrapper which is automatically filled in by Room.
 */
@Database(entities = {Event.class, Organisation.class}, version = 1, exportSchema = false)
public abstract class EventDatabase extends RoomDatabase {

    private static EventDatabase INSTANCE;

    /**
     * Get the singleton instance of EventDatabase and create it if needed.
     *
     * @param context from which the ApplicationContext will be retrieved
     * @return the EventDatabase singleton instance
     */
    public static EventDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventDatabase.class) {
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            EventDatabase.class, "event_database")
                            .build();
            }
        }

        return INSTANCE;
    }

    /**
     * Get a data access object for the Event type.
     *
     * A data access object is an object-oriented facade for accessing the data stored in the
     * database using the SQL query storied in the annotations in the DAO's definition.
     *
     * @return an {@link EventDao} for this database
     */
    public abstract EventDao eventDao();

    /**
     * Get a data access object for the Organisation type.
     *
     * @return an {@link OrganisationDao} for this database.
     */
    public abstract OrganisationDao organisationDao();
}
