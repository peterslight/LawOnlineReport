package com.peterstev.lawonlinereportnigeria.models.home;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;

/**
 * Created by Peterstev on 4/13/2018.
 * for LawOnlineReport
 */
@Entity(tableName = OfflineConstants.HOME_TABLE_NAME)
public class HomeModel {

    @SuppressWarnings("NullableProblems")
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }


    private String key;
    private String href;
    private String postTitle;


    @Ignore
    public HomeModel(String key, String href, String postTitle) {
        this.key = key;
        this.href = href;
        this.postTitle = postTitle;
    }

    public HomeModel() {

    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getHref() {
        return href;
    }

    public String getPostTitle() {
        return postTitle;
    }
}
