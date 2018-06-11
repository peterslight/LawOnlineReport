package com.peterstev.lawonlinereportnigeria.models.court_rules;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;

/**
 * Created by Peterstev on 26/04/2018.
 * for LawOnlineReport
 */
@Entity(tableName = OfflineConstants.COURT_RULES_TABLE_NAME)
public class CourtRulesModel {

    @SuppressWarnings("NullableProblems")
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @NonNull
    private String key;
    private String href;
    private String postTitle;

    @Ignore
    public CourtRulesModel(String key, String href, String postTitle) {
        this.key = key;
        this.href = href;
        this.postTitle = postTitle;
    }

    public CourtRulesModel() { }
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
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
