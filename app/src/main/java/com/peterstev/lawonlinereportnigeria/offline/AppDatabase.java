package com.peterstev.lawonlinereportnigeria.offline;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.BlogDAO;
import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.CourtRulesDAO;
import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.DirectoryDao;
import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.HomeDAO;
import com.peterstev.lawonlinereportnigeria.offline.dao_interfaces.LonDAO;
import com.peterstev.lawonlinereportnigeria.offline.schemas.BlogSchema;
import com.peterstev.lawonlinereportnigeria.offline.schemas.CourtSchema;
import com.peterstev.lawonlinereportnigeria.offline.schemas.DirectorySchema;
import com.peterstev.lawonlinereportnigeria.offline.schemas.HomeSchema;
import com.peterstev.lawonlinereportnigeria.offline.schemas.LonSchema;

/**
 * Created by Peterstev on 07/05/2018.
 * for LawOnlineReport
 */

@Database(entities = {CourtSchema.class, HomeSchema.class, BlogSchema.class, LonSchema.class, DirectorySchema.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CourtRulesDAO getCourtDAO();

    public abstract LonDAO getLawsOfNigeriaDAO();

    public abstract HomeDAO getHomeDAO();

    public abstract BlogDAO getBlogDAO();

    public abstract DirectoryDao getDirectoryDao();
}
