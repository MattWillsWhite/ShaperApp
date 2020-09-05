package com.example.aws.ShaperApp.Models;

import com.google.firebase.database.ServerValue;

public class Reaction {

    //declare reaction vars
    private String reaction;
    private Object timestamp;

    //constructors
    public Reaction() {
    }

    public Reaction(String reaction) {
        this.reaction = reaction;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    //getters and setters

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
