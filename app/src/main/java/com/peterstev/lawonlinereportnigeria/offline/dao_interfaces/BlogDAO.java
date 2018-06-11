package com.peterstev.lawonlinereportnigeria.offline.dao_interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;
import com.peterstev.lawonlinereportnigeria.offline.schemas.BlogSchema;

import java.util.List;

/**
 * Created by Peterstev on 07/05/2018.
 * for LawOnlineReport
 */
@Dao
public interface BlogDAO {
    @Insert
    void insert(BlogSchema... blogSchema);

    @Update
    void Update(BlogSchema... blogSchema);

    @Query("SELECT * FROM " + OfflineConstants.BLOG_TABLE_NAME)
    List<BlogSchema> getBlog();

    @Query("DELETE FROM " + OfflineConstants.BLOG_TABLE_NAME)
    void clearTable();
}
