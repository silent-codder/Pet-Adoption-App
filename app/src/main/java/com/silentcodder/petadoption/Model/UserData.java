package com.silentcodder.petadoption.Model;

public class UserData {
    String UserName;
    String ProfileImgUrl;
    String UserId;

    public UserData() {
    }

    public UserData(String userName, String profileImgUrl,String userId) {
        UserName = userName;
        ProfileImgUrl = profileImgUrl;
        UserId = userId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getProfileImgUrl() {
        return ProfileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        ProfileImgUrl = profileImgUrl;
    }
}
