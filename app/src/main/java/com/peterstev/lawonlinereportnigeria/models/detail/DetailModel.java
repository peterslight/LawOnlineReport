package com.peterstev.lawonlinereportnigeria.models.detail;

import java.util.ArrayList;

/**
 * Created by Peterstev on 4/16/2018.
 * for LawOnlineReport
 */

public class DetailModel {


    private String title;
    private String views;
    private String content;
    private String fullCaseLink;
    private ArrayList<RelatedPosts> relatedPosts;

    public ArrayList<RelatedPosts> getRelatedPosts() {
        return relatedPosts;
    }

    public void setRelatedPosts(ArrayList<RelatedPosts> relatedPosts) {
        this.relatedPosts = relatedPosts;
    }

    public String getFullCaseLink() {
        return fullCaseLink;
    }

    public void setFullCaseLink(String fullCaseLink) {
        this.fullCaseLink = fullCaseLink;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
