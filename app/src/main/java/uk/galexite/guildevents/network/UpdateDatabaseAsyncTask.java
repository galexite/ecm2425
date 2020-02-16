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

public class UpdateDatabaseAsyncTask extends AsyncTask<EventRepository, Void, Void> {

    private final Date lastUpdated;
    private final OnUpdateDatabaseCompleteListener onUpdateDatabaseCompleteListener;
    private Date updated = null; // the new update date

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

    public interface OnUpdateDatabaseCompleteListener {
        void onUpdateDatabaseComplete(Date updated);
    }
}
