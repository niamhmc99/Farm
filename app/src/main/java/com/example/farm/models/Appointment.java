package com.example.farm.models;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;

public class Appointment {

    private String id, userId;
    private String appTitle, appDescription, appDate;

    public Appointment(){ }

    public Appointment(String textTitle, String textDesc, String userId, String date){
        this.appTitle = textTitle;
        this.appDescription = textDesc;
        this.userId = userId;
        this.appDate = date;
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

    public String getAppDate() {
        return appDate;
    }

    public void setAppDate(String appDate) {
        this.appDate = appDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
