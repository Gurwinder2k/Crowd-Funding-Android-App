package com.example.myapplication;

public class Subscription {
    String user;
    String post;
    int amount;
    String bankCard;

    String key;
    String postUser;

    boolean isActive;
    //this is the subscription object that holds all the information about a subscription
    public Subscription(String user, String post, int amount, String bankCard, String key, String postUser) {
        this.user = user;
        this.post = post;
        this.amount = amount;
        this.bankCard = bankCard;
        this.key = key;
        this.isActive = true;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
