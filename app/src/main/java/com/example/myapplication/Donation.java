package com.example.myapplication;

public class Donation {

    private String user;
    private String post;
    private int amount;
    private String bankCard;
    private String postUser;

    private String key;

    //this is the donation object, holds all the details about a donation
    public Donation(String user, String post, int amount, String bankCard, String key, String postUser) {
        this.user = user;
        this.post = post;
        this.amount = amount;
        this.bankCard = bankCard;
        this.key = key;
        this.postUser = postUser;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
