package com.example.farm.adapters;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;

public class Appointment {

    private String id, appTitle, appDescription, userId;
    Date appDate;

    public Appointment(MaterialEditText textTitle, MaterialEditText textDesc, String userId, Date date){
        this.appTitle = appTitle;
        this.appDescription = appDescription;
        this.userId = userId;
        this.appDate = appDate;
    }

    public Appointment(String id, String appTitle, String appDescription, Date appDate) {
        this.id = id;
        this.appTitle = appTitle;
        this.appDescription = appDescription;
        this.appDate = appDate;
        this.userId = userId;

    }

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public Date getAppDate() {
        return appDate;
    }

    public void setAppDate(Date appDate) {
        this.appDate = appDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
