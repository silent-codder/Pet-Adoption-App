package com.silentcodder.petadoption.Model;

public class NotificationData {
    String PostId;
    String PostImgUrl;
    String PostUserId;
    long TimeStamp;
    String UserId;

    public NotificationData() {
    }

    public NotificationData(String postId, String postImgUrl, String postUserId, long timeStamp, String userId) {
        PostId = postId;
        PostImgUrl = postImgUrl;
        PostUserId = postUserId;
        TimeStamp = timeStamp;
        UserId = userId;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getPostImgUrl() {
        return PostImgUrl;
    }

    public void setPostImgUrl(String postImgUrl) {
        PostImgUrl = postImgUrl;
    }

    public String getPostUserId() {
        return PostUserId;
    }

    public void setPostUserId(String postUserId) {
        PostUserId = postUserId;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }
}

