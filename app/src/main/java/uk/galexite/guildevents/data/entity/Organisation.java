package uk.galexite.guildevents.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "organisation")
public class Organisation {

    /**
     * The organisation's identifier on the Guild's website.
     */
    @PrimaryKey
    private final int id;

    /**
     * Name of the organisation.
     */
    private final String name;

    public Organisation(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
