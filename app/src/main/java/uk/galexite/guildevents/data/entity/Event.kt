package uk.galexite.guildevents.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A concept of an Event that the Students Guild's societies and groups may host.
 */
@Entity(
    tableName = "event",
    indices = [Index(value = ["url"], unique = true), Index(value = ["organiserId"])],
    foreignKeys = [ForeignKey(
        entity = Organisation::class,
        parentColumns = ["id"],
        childColumns = ["organiserId"],
        onDelete = ForeignKey.CASCADE
    )]
)
class Event {
    /**
     * A unique identifier for the event.
     */
    @PrimaryKey(autoGenerate = true)
    var id = 0

    /**
     * The URL for the event. Uniquely identifies the event on the Guild's website.
     */
    var url: String? = null

    /**
     * The society or group that organises this event.
     */
    var organiserId = 0

    /**
     * The name of the society or group organising this event.
     */
    var organiserName: String? = null

    /**
     * The event's name.
     */
    var name: String? = null

    /**
     * Date and time for the event's start.
     */
    var fromDate: String? = null

    /**
     * The event's location. Can be null.
     */
    var location: String? = null

    /**
     * A short description of the event. Can be null.
     */
    var description: String? = null
}