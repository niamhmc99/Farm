package com.example.farm.adapters;

class Appointment {

    String id, appTitle, appDescription, appDate;

    public Appointment(){

    }

    public Appointment(String id, String appTitle, String appDescription, String appDate) {
        this.id = id;
        this.appTitle = appTitle;
        this.appDescription = appDescription;
        this.appDate = appDate;
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
