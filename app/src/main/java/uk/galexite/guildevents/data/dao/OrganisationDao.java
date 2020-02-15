package uk.galexite.guildevents.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import uk.galexite.guildevents.data.entity.Organisation;

@Dao
public interface OrganisationDao {

    /**
     * Add a new Organisation to the database.
     * @param organisation the Organisation to add
     */
    @Insert
    void insert(Organisation organisation);

    /**
     * Gets all the organisations stored in the database.
     *
     * @return a LiveData container containing a list of all the Organisation objects
     */
    @Query("SELECT * FROM organisation")
    LiveData<List<Organisation>> getAllOrganisations();
}
