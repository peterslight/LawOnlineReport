package com.peterstev.lawonlinereportnigeria.offline.dao_interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.peterstev.lawonlinereportnigeria.models.home.HomeModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.offline.schemas.HomeSchema;

import java.util.List;

/**
 * Created by Peterstev on 07/05/2018.
 * for LawOnlineReport
 */
@Dao
public interface HomeDAO {
    @Insert
    void insert(HomeSchema...homeSchema);

    @Update
    void Update(HomeSchema... homeSchema);


    @Query("SELECT * FROM " + OfflineConstants.HOME_TABLE_NAME)
    List<HomeModel> getHomeGlossary();

    @Query("DELETE FROM " + OfflineConstants.HOME_TABLE_NAME)
    void clearTable();
}
