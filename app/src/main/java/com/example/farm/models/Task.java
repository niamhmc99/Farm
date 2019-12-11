package com.example.farm.models;
import com.google.firebase.Timestamp;

public class Task {

    private String text;
    private boolean completed;
    private Timestamp created;
    private String userId;

    public Task() {
    }

    public Task(String text, boolean completed, Timestamp created, String userId) {
        this.text = text;
        this.completed = completed;
        this.created = created;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "text='" + text + '\'' +
                ", completed=" + completed +
                ", created=" + created +
                ", userId='" + userId + '\'' +
                '}';
    }
}
