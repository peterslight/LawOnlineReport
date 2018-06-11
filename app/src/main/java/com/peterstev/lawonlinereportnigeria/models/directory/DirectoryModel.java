package com.peterstev.lawonlinereportnigeria.models.directory;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Peterstev on 31/05/2018.
 * for LawOnlineReport
 */
public class DirectoryModel {
    @Ignore
    public DirectoryModel(String key, String href, String postTitle) {
        this.key = key;
        this.href = href;
        this.postTitle = postTitle;
    }

    public DirectoryModel() {

    }

    private String key;
    private String href;
    private String postTitle;

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
