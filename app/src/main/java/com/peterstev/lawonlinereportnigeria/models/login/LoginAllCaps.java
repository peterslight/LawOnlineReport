package com.peterstev.lawonlinereportnigeria.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Peterstev on 25/05/2018.
 * for LawOnlineReport
 */
public class LoginAllCaps {
    @SerializedName("read")
    @Expose
    private Boolean read;
    @SerializedName("level_0")
    @Expose
    private Boolean level0;
    @SerializedName("subscriber")
    @Expose
    private Boolean subscriber;

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getLevel0() {
        return level0;
    }

    public void setLevel0(Boolean level0) {
        this.level0 = level0;
    }

    public Boolean getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Boolean subscriber) {
        this.subscriber = subscriber;
    }
}
