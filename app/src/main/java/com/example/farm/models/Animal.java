package com.example.farm.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
@IgnoreExtraProperties
public class Animal {

    private String tagNumber;
    private String animalName;
    private String dob;
    private String sex;
    private String breed;
    private String dam;
    private String calvingDifficulty;
    private String aiORstockbull;
    private String sire;
    private String user_id; //admin_id left out of () atm

    public Animal(){ //empty constructor

    }

    public Animal(String tagNumber, String animalName, String dob, String sex, String breed, String dam, String calvingDifficulty, String aiORstockbull, String sire) {
        this.tagNumber = tagNumber;
        this.animalName = animalName;
        this.dob = dob;
        this.sex = sex;
        this.breed = breed;
        this.dam = dam;
        this.calvingDifficulty = calvingDifficulty;
        this.aiORstockbull = aiORstockbull;
        this.sire = sire;
    }

    public Animal(String tagNumber, String animalName, String dob, String sex, String breed, String dam, String calvingDifficulty, String aiORstockbull, String sire, String user_id) {
        this.tagNumber = tagNumber;
        this.animalName = animalName;
        this.dob = dob;
        this.sex = sex;
        this.breed = breed;
        this.dam = dam;
        this.calvingDifficulty = calvingDifficulty;
        this.aiORstockbull = aiORstockbull;
        this.sire = sire;
        this.user_id = user_id;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public void setTagNumber(String tagNumber) {
        this.tagNumber = tagNumber;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getDam() {
        return dam;
    }

    public void setDam(String dam) {
        this.dam = dam;
    }

    public String getCalvingDifficulty() {
        return calvingDifficulty;
    }

    public void setCalvingDifficulty(String calvingDifficulty) {
        this.calvingDifficulty = calvingDifficulty;
    }

    public String getAiORstockbull() {
        return aiORstockbull;
    }

    public void setAiORstockbull(String aiORstockbull) {
        this.aiORstockbull = aiORstockbull;
    }

    public String getSire() {
        return sire;
    }

    public void setSire(String sire) {
        this.sire = sire;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String admin_id) {
        this.user_id = user_id;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }
}
