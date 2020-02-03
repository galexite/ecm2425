package uk.galexite.guildevents.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;

import uk.galexite.guildevents.data.entity.Organisation;

@Dao
public interface OrganisationDao {

    @Insert
    void insert(Organisation organisation);
}
