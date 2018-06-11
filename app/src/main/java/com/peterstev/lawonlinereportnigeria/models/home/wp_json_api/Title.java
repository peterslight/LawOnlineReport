package com.peterstev.lawonlinereportnigeria.models.home.wp_json_api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by
 * Peterstev on 02/04/2018
 * for LawOnlineReport.com
 **/


public class Title {

    @SerializedName("rendered")
    @Expose
    private String rendered;

    public String getRendered() {
        return rendered;
    }

    public void setRendered(String rendered) {
        this.rendered = rendered;
    }

}