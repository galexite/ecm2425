package uk.galexite.guildevents.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * A concept of an Event that the Students Guild's societies and groups may host.
 */
@Entity(tableName = "event",
        indices = {@Index(value = "url", unique = true)} /*,
        foreignKeys = @ForeignKey(entity = Organisation.class,
                parentColumns = "id",
                childColumns = "organiserId",
                onDelete = CASCADE) */)
public class Event {

    /**
     * A unique identifier for the event.
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * The URL for the event. Uniquely identifies the event on the Guild's website.
     */
    private String url;

    /**
     * The society or group that organises this event.
     */
    private int organiserId;

    /**
     * The name of the society or group organising this event.
     */
    private String organiserName;

    /**
     * The event's name.
     */
    private String name;

    /**
     * Date and time for the event's start.
     */
    private String fromDate;

    /**
     * Date and time for when the event is scheduled to end. Can be null (indeterminate ending).
     */
    private String toDate;

    /**
     * The event's location. Can be null.
     */
    private String location;

    /**
     * A short description of the event. Can be null.
     */
    private String description;

    /**
     * Gets the unique identifier for the event.
     *
     * @return an integer uniquely identifying the event
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the identifier for the Organisation who organised this event.
     *
     * @return the primary key of the Organisation responsible for organising this event
     */
    public int getOrganiserId() {
        return organiserId;
    }

    /**
     * Gets the event's name.
     * @return the event's name
     */
    @NonNull
    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets a String describing the location of the event.
     *
     * @return the event's location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the event description.
     * @return the event's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the start date and time for the event.
     *
     * @return the starting Date
     */
    @NonNull
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(@NonNull String fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Gets the URL for the event on the Guild's website.
     *
     * @return the URL for the event
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets the name of the event's organiser,
     *
     * @return the organiser's name
     */
    public String getOrganiserName() {
        return organiserName;
    }

    /**
     * Gets the date (if available) when the event ends.
     *
     * @return the end date of the event
     */
    public String getToDate() {
        return toDate;
    }

    /**********************************************************************************************
     * SETTERS
     *
     * I won't be using these setters directly, but these will be used by Android Room and Firebase
     * to build instances of the Event object.
     *
     **********************************************************************************************/

    public void setUrl(String url) {
        this.url = url;
    }

    public void setOrganiserName(String organiserName) {
        this.organiserName = organiserName;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public void setOrganiserId(int organiserId) {
        this.organiserId = organiserId;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
