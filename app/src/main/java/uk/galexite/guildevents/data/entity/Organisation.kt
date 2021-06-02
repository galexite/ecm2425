package uk.galexite.guildevents.data.entity;

import androidx.annotation.NonNull;
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

    /**
     * Gets the unique identifier for this organisation.
     *
     * @return an integer uniquely identifying this organisation on the Guild website
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of this organisation.
     *
     * @return the organisation's name
     */
    public String getName() {
        return name;
    }

    /**
     * Build a String representing the Organisation by taking the organisation's name.
     * <p>
     * Used for the ArrayAdapter.
     */
    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
