package uk.galexite.guildevents.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "organisation")
class Organisation(
    /**
     * The organisation's identifier on the Guild's website.
     */
    @field:PrimaryKey val id: Int,
    /**
     * Name of the organisation.
     */
    val name: String
)