package com.example.farm.placesOfInterest;

public class Place {
    public String name;
    public String location;
    public String userLocation;
    public String website;
    public String placeID;

    public double rating;

    public Place(String name, String location, double rating, String placeID, String currLocation) {
        this.name = name;
        this.location = location;
        this.rating = rating;
        this.placeID = placeID;
        this.userLocation = currLocation;
    }
    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
