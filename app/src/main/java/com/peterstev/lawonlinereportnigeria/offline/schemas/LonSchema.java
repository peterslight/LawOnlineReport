package com.peterstev.lawonlinereportnigeria.offline.schemas;

import android.arch.persistence.room.Entity;

import com.peterstev.lawonlinereportnigeria.models.lon.LonModel;
import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;

/**
 * Created by Peterstev on 07/05/2018.
 * for LawOnlineReport
 */

@Entity(tableName = OfflineConstants.LON_TABLE_NAME)
public class LonSchema extends LonModel {

    @Override
    public String getHref() {
        return super.getHref();
    }

    @Override
    public String getKey() {
        return super.getKey();
    }

    @Override
    public String getPostTitle() {
        return super.getPostTitle();
    }

    @Override
    public void setHref(String href) {
        super.setHref(href);
    }

    @Override
    public void setKey(String key) {
        super.setKey(key);
    }

    @Override
    public void setPostTitle(String postTitle) {
        super.setPostTitle(postTitle);
    }
}
