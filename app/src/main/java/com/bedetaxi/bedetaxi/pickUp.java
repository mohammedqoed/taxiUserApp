package com.bedetaxi.bedetaxi;

/**
 * Created by LENOVO on 12/25/2016.
 */

public class pickUp {

    public String Name;
    public int Distance;
    public String BName;
    public String Secound;
    public boolean flag;

    public pickUp(String name, int distance){
        this.Distance=distance;
        this.Name=name;

    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public pickUp(String name , String art, boolean flag){
        this.BName=name;
        this.Secound=art;
        this.flag=flag;
    }

    public String getSecound() {
        return Secound;
    }

    public void setSecound(String secound) {
        Secound = secound;
    }

    public String getName() {
        return Name;
    }

    public String getBName() {
        return BName;
    }

    public void setBName(String BName) {
        this.BName = BName;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getDistance() {
        return Distance;
    }

    public void setDistance(int distance) {
        Distance = distance;
    }
}
