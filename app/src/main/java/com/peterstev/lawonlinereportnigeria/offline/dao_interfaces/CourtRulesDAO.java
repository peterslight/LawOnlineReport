package com.peterstev.lawonlinereportnigeria.offline.dao_interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.peterstev.lawonlinereportnigeria.models.court_rules.CourtRulesModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.offline.schemas.CourtSchema;

import java.util.List;

/**
 * Created by Peterstev on 07/05/2018.
 * for LawOnlineReport
 */
@Dao
public interface CourtRulesDAO {

    @Insert
    void insert(CourtSchema... courtRulesModel);

    @Update
    void Update(CourtSchema... courtRulesModel);

    @Query("SELECT * FROM " + OfflineConstants.COURT_RULES_TABLE_NAME)
    List<CourtRulesModel> getCourtRules();

    @Query("DELETE FROM " + OfflineConstants.COURT_RULES_TABLE_NAME)
    void clearTable();
}