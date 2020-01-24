package com.bignerdranch.android.criminatintent;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID id;
    private String title;
    private Date date;
    private boolean solved;

    public String getSuspect() {
        return suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }

    private String suspect;

    public Crime() {
        this(UUID.randomUUID());
    }
    public Crime(UUID id) {
        this.id = id;
        date = new Date();
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }


    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}