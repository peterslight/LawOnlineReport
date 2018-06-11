package com.peterstev.lawonlinereportnigeria.offline.schemas;

import android.arch.persistence.room.Entity;

import com.peterstev.lawonlinereportnigeria.models.court_rules.CourtRulesModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;

/**
 * Created by Peterstev on 07/05/2018.
 * for LawOnlineReport
 */

@Entity(tableName = OfflineConstants.COURT_RULES_TABLE_NAME)
public class CourtSchema extends CourtRulesModel {
}
