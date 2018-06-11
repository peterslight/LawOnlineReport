package com.peterstev.lawonlinereportnigeria.models.home;

import java.util.ArrayList;

/**
 * Created by Peterstev on 4/14/2018.
 * for LawOnlineReport
 */

public class CategoryModel {
    private String title;
    private String excerpt;
    private String date;
    private String author;
    private String href;
    private ArrayList<String> tags;

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
