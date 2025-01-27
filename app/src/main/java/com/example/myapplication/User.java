package com.example.myapplication;

public class User {
    private String fname, lname, email, phnumber;
    private int Tdonations, Tsubscriptions, drinkPoints, badgePoints;

    //this is the user object, it stored all the details about a user
    public User(String fname, String lname, String email, String phnumber, int Tdonations, int Tsubscriptions, int drinkPoints, int badgePoints) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.phnumber = phnumber;
        this.Tdonations = Tdonations;
        this.Tsubscriptions = Tsubscriptions;
        this.drinkPoints = drinkPoints;
        this.badgePoints = badgePoints;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhnumber() {
        return phnumber;
    }

    public void setPhnumber(String phnumber) {
        this.phnumber = phnumber;
    }

    public int getTdonations() {
        return Tdonations;
    }

    public void setTdonations(){
        Tdonations +=1;
    }

    public int getTsubscriptions() {
        return Tsubscriptions;
    }

    public void setTsubscription(){
        Tsubscriptions +=1;
    }


    public int getDrinkPoints() {
        return drinkPoints;
    }

    public void setDrinkPoints(int drinkPoints) {
        this.drinkPoints += drinkPoints;
    }

    public int getBadgePoints() {
        return badgePoints;
    }

    public void setBadgePoints(int badgePoints) {
        this.badgePoints += badgePoints;
    }
}
