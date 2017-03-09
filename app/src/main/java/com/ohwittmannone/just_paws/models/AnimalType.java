package com.ohwittmannone.just_paws.models;

/**
 * Created by Courtney on 2016-10-24.
 */

public class AnimalType {
    private String petInfo;
    private String imgURL;
    private String petName;
    private String id;

    public AnimalType(){
    }

    public String getPetInfo() {
        return petInfo;
    }

    public void setPetInfo(String petInfo) {
        this.petInfo = petInfo;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgResID) {
        this.imgURL = imgResID;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
