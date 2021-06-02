package uk.galexite.guildevents.network;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import uk.galexite.guildevents.data.entity.Event;
import uk.galexite.guildevents.data.entity.Organisation;
import uk.galexite.guildevents.data.repository.EventRepository;

/**
 * An AsyncTask to update the database if there is a newer JSON file on the server than the one that
 * was previously seen. This is run every time the ListActivity starts as it uses barely any data
 * when identifying when the database needs to be updated, and only updates the data when necessary.
 * <p>
 * If there is no internet connection, this silently fails and the existing data in the database is
 * then used until next time the user opens the list activity on an Internet connection. This allows
 * the user to use the app without issue when offline.
 */
public class UpdateDatabaseAsyncTask extends AsyncTask<EventRepository, Void, Void> {

    /**
     * The date of the file on the server last used to update the database. If the version on the
     * server is newer than this update, this AsyncTask will update the database with the new data.
     */
    private final Date lastUpdated;
    /**
     * This listener is run on the UI thread in onPostExecute(), to allow the activity to update its
     * views to reflect that the update has completed (i.e. by removing the ProgressBar from view).
     */
    private final OnUpdateDatabaseCompleteListener onUpdateDatabaseCompleteListener;
    /**
     * This is the new date which will replace lastUpdated, otherwise null if we did not find a
     * newer JSON file on the server.
     */
    private Date updated = null;

    public UpdateDatabaseAsyncTask(Date lastUpdated,
                                   OnUpdateDatabaseCompleteListener listener) {
        this.lastUpdated = lastUpdated;
        this.onUpdateDatabaseCompleteListener = listener;
    }

    @Override
    protected Void doInBackground(EventRepository... repositories) {
        final EventRepository repository = repositories[0];

        final Gson gson = new GsonBuilder().create();

        final Date organisationLastModified = GuildEventsS3.getOrganisationsLastModified();
        if (organisationLastModified == null) {
            return null; // we don't have Internet!
        }

        // Only update the Organisations database if there is a newer JSON file than the one we have
        // seen previously! Saves on data when the user has already used the app with Internet.
        if (lastUpdated.before(organisationLastModified)) {
            final String organisationJson = GuildEventsS3.getOrganisationsJson();
            if (organisationJson == null) {
                return null;
            }

            // Decode the JSON in to the Organisation Java class.
            // The TypeToken class is needed to work around type erasure from putting the type in
            // a list.
            Type organisationListType = new TypeToken<List<Organisation>>() {
            }.getType();
            final List<Organisation> organisations
                    = gson.fromJson(organisationJson, organisationListType);

            for (Organisation organisation : organisations)
                repository.insertOrganisationSynchronous(organisation);
        }

        final Date eventsLastModified = GuildEventsS3.getEventsJsonLastModified();
        if (eventsLastModified == null) {
            return null;
        }

        if (lastUpdated.before(eventsLastModified)) {
            final String eventsJson = GuildEventsS3.getEventsJson();
            if (eventsJson == null) {
                return null;
            }

            // Decode the JSON in to Events.
            Type eventListType = new TypeToken<List<Event>>() {
            }.getType();
            final List<Event> events = gson.fromJson(eventsJson, eventListType);

            for (Event event : events)
                repository.insertEventSynchronous(event);
        }

        updated = organisationLastModified.before(eventsLastModified) ? eventsLastModified
                : organisationLastModified;

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // Call the listener
        onUpdateDatabaseCompleteListener.onUpdateDatabaseComplete(updated);
    }

    /**
     * This lambda runs on the UI thread (in onPostExecute) to allow the UI to update (i.e. by
     * dropping the ProgressBar from view) in the Activity to reflect the task's completion.
     */
    public interface OnUpdateDatabaseCompleteListener {
        void onUpdateDatabaseComplete(Date updated);
    }
}
