package com.peterstev.lawonlinereportnigeria.offline.dao_interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.peterstev.lawonlinereportnigeria.models.directory.DirectoryModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.offline.schemas.DirectorySchema;

import java.util.List;

/**
 * Created by Peterstev on 31/05/2018.
 * for LawOnlineReport
 */
@Dao
public interface DirectoryDao {
    @Insert
    void insert(DirectorySchema... directorySchema);

    @Update
    void Update(DirectorySchema... directorySchema);

    @Query("SELECT * FROM " + OfflineConstants.DIRECTORY_TABLE_NAME)
    List<DirectoryModel> getLawOnlineDirectory();

    @Query("DELETE FROM " + OfflineConstants.DIRECTORY_TABLE_NAME)
    void clearTable();
}
