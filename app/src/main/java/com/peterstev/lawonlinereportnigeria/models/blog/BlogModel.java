package com.peterstev.lawonlinereportnigeria.models.blog;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.peterstev.lawonlinereportnigeria.offline.OfflineConstants;

/**
 * Created by Peterstev on 07/05/2018.
 * for LawOnlineReport
 */

@Entity(tableName = OfflineConstants.BLOG_TABLE_NAME)
public class BlogModel {

    private String title;
    private String excerpt;
    private String date;
    private String author;
    private String href;

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


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}
