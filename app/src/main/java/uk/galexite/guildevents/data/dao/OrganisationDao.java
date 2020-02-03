package uk.galexite.guildevents.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import uk.galexite.guildevents.data.entity.Organisation;

@Dao
public interface OrganisationDao {

    /**
     * Add a new Organisation to the database.
     *
     * @param organisation the Organisation to add
     */
    @Insert
    void insert(Organisation organisation);
}
