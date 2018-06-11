package com.peterstev.lawonlinereportnigeria.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Peterstev on 25/05/2018.
 * for LawOnlineReport
 */

public class LoginModel {
    @SerializedName("data")
    @Expose
    private LoginData data;
    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("caps")
    @Expose
    private LoginCaps caps;
    @SerializedName("cap_key")
    @Expose
    private String capKey;
    @SerializedName("roles")
    @Expose
    private List<String> roles = null;
    @SerializedName("allcaps")
    @Expose
    private LoginAllCaps allcaps;
    @SerializedName("filter")
    @Expose
    private Object filter;

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public LoginCaps getCaps() {
        return caps;
    }

    public void setCaps(LoginCaps caps) {
        this.caps = caps;
    }

    public String getCapKey() {
        return capKey;
    }

    public void setCapKey(String capKey) {
        this.capKey = capKey;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public LoginAllCaps getAllcaps() {
        return allcaps;
    }

    public void setAllcaps(LoginAllCaps allcaps) {
        this.allcaps = allcaps;
    }

    public Object getFilter() {
        return filter;
    }

    public void setFilter(Object filter) {
        this.filter = filter;
    }
}
