package com.example.myapplication;

public class Comment {

    private String comment, commentUser, name;

    //This is the comment object, holds all the details about a comment
    public Comment(String comment, String userId, String name) {
        this.comment = comment;
        this.commentUser = userId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentUserId() {
        return commentUser;
    }

    public void setCommentUserId(String commentUser) {
        this.commentUser = commentUser;
    }


}
