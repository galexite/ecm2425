package uk.galexite.guildevents.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

/**
 * A concept of an Event that the Students Guild's societies and groups may host.
 */
@Entity(tableName = "event",
        indices = {@Index("organiserId")},
        foreignKeys = @ForeignKey(entity = Organisation.class,
                parentColumns = "id",
                childColumns = "organiserId",
                onDelete = CASCADE))
public class Event {

    /**
     * A unique identifier for the event on the Guild's website.
     */
    @PrimaryKey
    private final int id;

    /**
     * The society or group that organises this event.
     */
    private final int organiserId;

    /**
     * The event's name.
     */
    @NonNull
    private final String name;

    /**
     * Date and time for the event's start.
     */
    @NonNull
    private final Date date;

    /**
     * The event's location. Can be null.
     */
    private final String location;

    /**
     * A short description of the event. Can be null.
     */
    private final String description;

    public Event(int id, int organiserId, @NonNull String name, @NonNull Date date,
                 String location, String description) {
        this.id = id;
        this.organiserId = organiserId;
        this.name = name;
        this.date = date;
        this.location = location;
        this.description = description;
    }

    /**
     * Gets the unique identifier for the event on the Guild's website.
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
     *
     * @return the event's name
     */
    @NonNull
    public String getName() {
        return name;
    }

    /**
     * Get the start date and time for the event.
     *
     * @return the starting Date
     */
    @NonNull
    public Date getDate() {
        return date;
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
     *
     * @return the event's description
     */
    public String getDescription() {
        return description;
    }
}
