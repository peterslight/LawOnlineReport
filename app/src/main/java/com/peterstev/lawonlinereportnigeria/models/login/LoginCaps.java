package com.peterstev.lawonlinereportnigeria.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Peterstev on 25/05/2018.
 * for LawOnlineReport
 */
public class LoginCaps {
    @SerializedName("subscriber")
    @Expose
    private Boolean subscriber;

    public Boolean getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Boolean subscriber) {
        this.subscriber = subscriber;
    }
}
