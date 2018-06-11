package com.peterstev.lawonlinereportnigeria.offline.dao_interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.peterstev.lawonlinereportnigeria.models.lon.LonModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.offline.schemas.LonSchema;

import java.util.List;

/**
 * Created by Peterstev on 07/05/2018.
 * for LawOnlineReport
 */

@Dao
public interface LonDAO {
    @Insert
    void insert(LonSchema... lonSchema);

    @Update
    void Update(LonSchema... lonSchema);

    @Query("SELECT * FROM " + OfflineConstants.LON_TABLE_NAME)
    List<LonModel> getLawsOfNigeria();

    @Query("DELETE FROM " + OfflineConstants.LON_TABLE_NAME)
    void clearTable();
}
