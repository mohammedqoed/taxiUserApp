package com.bedetaxi.bedetaxi;

/**
 * Created by Alaa on 5/7/2017.
 */
public class HistoryList {
    String pickUp;
    String dest;
    String date;
    String drive;

    public HistoryList(String pickUp, String date, String dest, String drive) {
        this.pickUp = pickUp;
        this.date = date;
        this.dest = dest;
        this.drive = drive;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getDrive() {
        return drive;
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    public String getPickUp() {
        return pickUp;
    }

    public void setPickUp(String pickUp) {
        this.pickUp = pickUp;
    }
}
