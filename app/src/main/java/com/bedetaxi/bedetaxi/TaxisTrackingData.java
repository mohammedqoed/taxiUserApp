package com.bedetaxi.bedetaxi;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

import org.osmdroid.views.overlay.Marker;

/**
 * Created by LENOVO on 7/3/2017.
 */

public class TaxisTrackingData {

    String taxiID;
    Marker taxiMarker;
    Firebase firebase;
    ValueEventListener valueEventListener;
    int isAvailable;
    boolean isRemoved = false;

    public ValueEventListener getValueEventListener() {
        return valueEventListener;
    }

    public int getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(int isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setValueEventListener(ValueEventListener valueEventListener) {
        this.valueEventListener = valueEventListener;
    }

    public TaxisTrackingData(String id){
        this.taxiID=id;
    }

    public String getTaxiID() {
        return taxiID;
    }

    public void setTaxiID(String taxiID) {
        this.taxiID = taxiID;
    }

    public Marker getTaxiMarker() {
        return taxiMarker;
    }

    public void setTaxiMarker(Marker taxiMarker) {
        this.taxiMarker = taxiMarker;
    }

    public void setFirebase(Firebase firebase) {
        this.firebase = firebase;
    }

    public Firebase getFirebase() {
        return firebase;
    }

    public void setIsRemoved(boolean isRemoved) {
        this.isRemoved = isRemoved;
    }
    public boolean isReomved(){
        return isRemoved;
    }
}
