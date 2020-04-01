package com.example.farm.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;
@IgnoreExtraProperties
public class Animal implements Serializable {

    private String id;
    private String tagNumber;
    private String animalName;
    private String dob;
    private String sex;
    private String gender;
    private String breed;
    private String dam;
    private String calvingDifficulty;
    private String aiORstockbull;
    private String sire;
    private String user_id; //admin_id left out of () atm
    private Timestamp timeAdded;



    private String animalProfilePic;


    public Animal(){ //empty constructor

    }

    //this constructor includes animal profile pic and userID for RecycleView
    public Animal(String id, String tagNumber, String animalName, String dob, String sex, String gender, String breed, String dam, String calvingDifficulty, String aiORstockbull, String sire, String user_id, String animalProfilePic, Timestamp timeAdded) {
        this.id = id;
        this.tagNumber = tagNumber;
        this.animalName = animalName;
        this.dob = dob;
        this.sex = sex;
        this.gender = gender;
        this.breed = breed;
        this.dam = dam;
        this.calvingDifficulty = calvingDifficulty;
        this.aiORstockbull = aiORstockbull;
        this.sire = sire;
        this.user_id = user_id;
        this.animalProfilePic = animalProfilePic;
        this.timeAdded = timeAdded;
    }

    public Animal(String tagNumber, String animalName, String dob, String gender, String breed, String dam, String calvingDifficulty, String aiORstockbull, String sire) {
        this.tagNumber = tagNumber;
        this.animalName = animalName;
        this.dob = dob;
        this.gender = gender;
        this.breed = breed;
        this.dam = dam;
        this.calvingDifficulty = calvingDifficulty;
        this.aiORstockbull = aiORstockbull;
        this.sire = sire;
    }

    public Animal(String id, String tagNumber, String animalName, String dob, String sex, String breed, String dam, String calvingDifficulty, String aiORstockbull, String sire, String user_id) {
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAnimalProfilePic() {
        return animalProfilePic;
    }

    public void setAnimalProfilePic(String animalProfilePic) {
        this.animalProfilePic = animalProfilePic;
    }
    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

}
