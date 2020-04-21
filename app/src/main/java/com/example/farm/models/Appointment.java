package com.example.farm.models;

public class Appointment {

    private String userId, appTitle, appDescription, appDate;

    public Appointment(){

    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
