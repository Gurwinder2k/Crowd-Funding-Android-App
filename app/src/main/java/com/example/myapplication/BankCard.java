package com.example.myapplication;

import java.io.Serializable;

public class BankCard implements Serializable {

    private boolean isChecked = false;
    private String cardNum, nameOnCard, expDate, cvvCode, key;

//This is the bank card object that holds all the information for each card
    public BankCard(boolean isChecked, String cardNum, String nameOnCard, String expDate, String cvvCode, String key) {
        this.isChecked = isChecked;
        this.cardNum = cardNum;
        this.nameOnCard = nameOnCard;
        this.expDate = expDate;
        this.cvvCode = cvvCode;
        this.key = key;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
