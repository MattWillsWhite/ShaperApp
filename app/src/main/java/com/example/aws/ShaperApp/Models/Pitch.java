package com.example.aws.ShaperApp.Models;

import com.google.firebase.database.ServerValue;

public class Pitch {

    //declare pitch vars
    private String pitchKey;
    private String title;
    private String description;
    private String picture;
    private String userId;
    private String userPhoto;
    private Long popularity;
    private String appetite;
    private String problem;
    private String rabbitHole;
    private String noGoes;
    private String success;
    private Long status;


    private Object timeStamp ;

    //constructor
    public Pitch(String title, String description, String picture, String userId, String userPhoto,
                 String appetite, String problem, String rabbitHole, String noGoes, String success,
                 Long popularity, Long status) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.popularity = popularity;
        this.appetite = appetite;
        this.problem = problem;
        this.rabbitHole = rabbitHole;
        this.noGoes = noGoes;
        this.success = success;
        this.status = status;
        this.timeStamp = ServerValue.TIMESTAMP;
    }

   //empty contructor
    public Pitch() {
    }

    //getter and setters
    public String getPitchKey() {
        return pitchKey;
    }

    public void setPitchKey(String pitchKey) {
        this.pitchKey = pitchKey;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Long getPopularity() {
        return popularity;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public String getAppetite() {
        return appetite;
    }

    public String getProblem() {
        return problem;
    }

    public String getRabbitHole() {
        return rabbitHole;
    }

    public String getNoGoes() {
        return noGoes;
    }

    public String getSuccess() {
        return success;
    }

    public Long getStatus() {
        return status;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public void setPopularity(Long popularity) {
        this.popularity = popularity;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setAppetite(String appetite) {
        this.appetite = appetite;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public void setRabbitHole(String rabbitHole) {
        this.rabbitHole = rabbitHole;
    }

    public void setNoGoes(String noGoes) {
        this.noGoes = noGoes;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public void setStatus(Long status) {
        this.status = status;
    }


}
