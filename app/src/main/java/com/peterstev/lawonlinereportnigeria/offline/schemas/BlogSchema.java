package com.peterstev.lawonlinereportnigeria.offline.schemas;

import android.arch.persistence.room.Entity;

import com.peterstev.lawonlinereportnigeria.models.blog.BlogModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;

/**
 * Created by Peterstev on 07/05/2018.
 * for LawOnlineReport
 */
@Entity(tableName = OfflineConstants.BLOG_TABLE_NAME)
public class BlogSchema extends BlogModel{

}
