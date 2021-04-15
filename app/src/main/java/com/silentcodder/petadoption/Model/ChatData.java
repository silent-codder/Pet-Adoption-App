package com.silentcodder.petadoption.Model;

public class ChatData {
    String Msg;
    String ReceiverId;
    String SenderId;
    String UserId;
    String ChatId;
    String ProfileImgUrl;
    Long TimeStamp;

    public ChatData() {
    }

    public ChatData(String msg, String receiverId, String senderId, Long timeStamp,String userId,String chatId,String profileImgUrl) {
        Msg = msg;
        ReceiverId = receiverId;
        SenderId = senderId;
        TimeStamp = timeStamp;
        UserId = userId;
        ChatId = chatId;
        ProfileImgUrl = profileImgUrl;
    }


    public String getProfileImgUrl() {
        return ProfileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        ProfileImgUrl = profileImgUrl;
    }

    public String getChatId() {
        return ChatId;
    }

    public void setChatId(String chatId) {
        ChatId = chatId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getReceiverId() {
        return ReceiverId;
    }

    public void setReceiverId(String receiverId) {
        ReceiverId = receiverId;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }
}
