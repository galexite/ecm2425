package uk.galexite.guildevents.network

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import uk.galexite.guildevents.data.entity.Event
import uk.galexite.guildevents.data.entity.Organisation
import uk.galexite.guildevents.data.repository.EventRepository
import java.util.*

/**
 * An AsyncTask to update the database if there is a newer JSON file on the server than the one that
 * was previously seen. This is run every time the ListActivity starts as it uses barely any data
 * when identifying when the database needs to be updated, and only updates the data when necessary.
 *
 *
 * If there is no internet connection, this silently fails and the existing data in the database is
 * then used until next time the user opens the list activity on an Internet connection. This allows
 * the user to use the app without issue when offline.
 */
suspend fun updateDatabaseTask(
    /**
     * The date of the file on the server last used to update the database. If the version on the
     * server is newer than this update, this AsyncTask will update the database with the new data.
     */
    lastUpdated: Date,
    /**
     * The date of the file on the server last used to update the database. If the version on the
     * server is newer than this update, this AsyncTask will update the database with the new data.
     */
    repository: EventRepository
): Date? = withContext(Dispatchers.IO) {
    val gson = GsonBuilder().create()

    // Only update the Organisations database if there is a newer JSON file than the one we have
    // seen previously! Saves on data when the user has already used the app with Internet.
    val organisationLastModified = GuildEventsS3.organisationsLastModified
        ?: return@withContext null
    if (lastUpdated.before(organisationLastModified)) {
        val organisationJson = async { GuildEventsS3.organisationsJson }

        // Decode the JSON in to the Organisation Java class.
        // The TypeToken class is needed to work around type erasure from putting the type in
        // a list.
        val organisationListType = object : TypeToken<Array<Organisation>>() {}.type
        val organisations =
            gson.fromJson<Array<Organisation>>(organisationJson.await(), organisationListType)

        repository.insertAllOrganisations(*organisations)
    }

    val eventsLastModified = GuildEventsS3.eventsJsonLastModified ?: return@withContext null
    if (lastUpdated.before(eventsLastModified)) {
        val eventsJson = async { GuildEventsS3.eventsJson }

        // Decode the JSON in to Events.
        val eventListType = object : TypeToken<Array<Event?>?>() {}.type
        val events = gson.fromJson<Array<Event>>(eventsJson.await(), eventListType)

        repository.insertAllEvents(*events)
    }

    return@withContext if (organisationLastModified.before(eventsLastModified))
        eventsLastModified
    else
        organisationLastModified
}