package com.silentcodder.petadoption.Model;

public class PostData extends PostId{
    String About;
    String Age;
    String Category;
    String PetImgUrl;
    String PetName;
    String Sex;
    String UserId;
    Long TimeStamp;
    String Comment;

    public PostData() {
    }

    public PostData(String about, String age, String category, String petImgUrl, String petName, String sex, String userId, Long timeStamp,String comment) {
        About = about;
        Age = age;
        Category = category;
        PetImgUrl = petImgUrl;
        PetName = petName;
        Sex = sex;
        UserId = userId;
        TimeStamp = timeStamp;
        Comment = comment;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPetImgUrl() {
        return PetImgUrl;
    }

    public void setPetImgUrl(String petImgUrl) {
        PetImgUrl = petImgUrl;
    }

    public String getPetName() {
        return PetName;
    }

    public void setPetName(String petName) {
        PetName = petName;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public Long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        TimeStamp = timeStamp;
    }
}
