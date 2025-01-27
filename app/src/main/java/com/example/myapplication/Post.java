package com.example.myapplication;

import java.io.Serializable;
import java.util.List;

public class Post implements Serializable {

    private String postTitleText, postDescriptionText,
            doornumText, streetnameText, postcodeText, postId, userId;

    private int amountFunded, fundingamountText;

    private boolean goalReached = false;
    private List<String> imageNamesList;

    //this is the Post object that holds all the details about a post
    public Post(String postId, String userId, String postTitleText, String postDescriptionText, int fundingamountText, String doornumText, String streetnameText, String postcodeText, List<String> imageNamesList, int amountFunded, boolean goalReached) {
        this.postId = postId;
        this.postTitleText = postTitleText;
        this.postDescriptionText = postDescriptionText;
        this.fundingamountText = fundingamountText;
        this.doornumText = doornumText;
        this.streetnameText = streetnameText;
        this.postcodeText = postcodeText;
        this.imageNamesList = imageNamesList;
        this.amountFunded = amountFunded;
        this.userId = userId;
        this.goalReached = goalReached;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostTitleText() {
        return postTitleText;
    }

    public String getPostDescriptionText() {
        return postDescriptionText;
    }

    public int getFundingamountText() {
        return fundingamountText;
    }

    public String getDoornumText() {
        return doornumText;
    }

    public String getStreetnameText() {
        return streetnameText;
    }

    public String getPostcodeText() {
        return postcodeText;
    }


    public void setPostTitleText(String postTitleText) {
        this.postTitleText = postTitleText;
    }

    public void setPostDescriptionText(String postDescriptionText) {
        this.postDescriptionText = postDescriptionText;
    }

    public void setFundingamountText(int fundingamountText) {
        this.fundingamountText = fundingamountText;
    }

    public void setDoornumText(String doornumText) {
        this.doornumText = doornumText;
    }

    public void setStreetnameText(String streetnameText) {
        this.streetnameText = streetnameText;
    }

    public void setPostcodeText(String postcodeText) {
        this.postcodeText = postcodeText;
    }

    public void setImageNames(List<String> imageNamesList) {
        this.imageNamesList = imageNamesList;
    }

    public List<String> getImageNamesList() {
        return imageNamesList;
    }

    public int getImageListLength(){
        return imageNamesList.size();
    }

    public int getAmountFunded() {
        return amountFunded;
    }

    public void setAmountFunded(int amountFunded) {
        this.amountFunded = amountFunded;
    }

    public boolean isGoalReached() {
        return goalReached;
    }

    public void setGoalReached(boolean goalReached) {
        this.goalReached = goalReached;
    }
}
