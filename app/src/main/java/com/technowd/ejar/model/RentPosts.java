package com.technowd.ejar.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class RentPosts implements Serializable {
    public String documentId, latitude, longitude,new_rent_price,new_rent_room_numbers,
            new_rent_state,new_rent_type, place,time,user_id,rent_desc,user_set_rent_image,daily_or_monthly;
    public int place_index;
    public boolean isVacant;

    public RentPosts(){

    }

    public RentPosts(String documentId ,int place_index ,String daily_or_monthly ,
                     String user_set_rent_image,String rent_desc, String latitude,
                     String longitude, String new_rent_price, String new_rent_room_numbers,
                     String new_rent_state, String new_rent_type, String place, String time, String user_id,boolean isVacant) {
        this.documentId = documentId;
        this.latitude = latitude;
        this.place_index = place_index;
        this.rent_desc = rent_desc;
        this.longitude = longitude;
        this.user_set_rent_image = user_set_rent_image;
        this.new_rent_price = new_rent_price;
        this.new_rent_room_numbers = new_rent_room_numbers;
        this.new_rent_state = new_rent_state;
        this.new_rent_type = new_rent_type;
        this.place = place;
        this.time = time;
        this.user_id = user_id;
        this.daily_or_monthly = daily_or_monthly;
        this.isVacant = isVacant;
    }
    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public boolean isVacant() {
        return isVacant;
    }

    public void setVacant(boolean vacant) {
        isVacant = vacant;
    }

    public int getPlace_index() {
        return place_index;
    }

    public void setPlace_index(int place_index) {
        this.place_index = place_index;
    }

    public String getUser_set_rent_image() {
        return user_set_rent_image;
    }

    public void setUser_set_rent_image(String user_set_rent_image) {
        this.user_set_rent_image = user_set_rent_image;
    }
    public String getRent_desc() {
        return rent_desc;
    }

    public void setRent_desc(String rent_desc) {
        this.rent_desc = rent_desc;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNew_rent_price() {
        return new_rent_price;
    }

    public void setNew_rent_price(String new_rent_price) {
        this.new_rent_price = new_rent_price;
    }

    public String getNew_rent_room_numbers() {
        return new_rent_room_numbers;
    }

    public void setNew_rent_room_numbers(String new_rent_room_numbers) {
        this.new_rent_room_numbers = new_rent_room_numbers;
    }

    public String getNew_rent_state() {
        return new_rent_state;
    }

    public void setNew_rent_state(String new_rent_state) {
        this.new_rent_state = new_rent_state;
    }

    public String getNew_rent_type() {
        return new_rent_type;
    }

    public void setNew_rent_type(String new_rent_type) {
        this.new_rent_type = new_rent_type;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDaily_or_monthly() {
        return daily_or_monthly;
    }

    public void setDaily_or_monthly(String daily_or_monthly) {
        this.daily_or_monthly = daily_or_monthly;
    }
}
