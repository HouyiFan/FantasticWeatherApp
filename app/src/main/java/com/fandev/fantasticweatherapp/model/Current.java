package com.fandev.fantasticweatherapp.model;

import java.io.Serializable;

public class Current implements Serializable {

    // under "weather" attribute
    private String description;
    private String icon;

    // under root object
    private String cityName;
    private String date;

    // under "sys" attribute
    private String countryName;
    private String sunrise;
    private String sunset;

    // under "main" attribute
    private String curTemp;
    private String humidity;

    // under "wind" attribute
    private String speed;

    private String photoRef;

    public Current() {
    }

    public Current(String description, String icon, String cityName, String date, String countryName, String sunrise, String sunset, String curTemp, String humidity, String speed) {
        this.description = description;
        this.icon = icon;
        this.cityName = cityName;
        this.date = date;
        this.countryName = countryName;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.curTemp = curTemp;
        this.humidity = humidity;
        this.speed = speed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getCurTemp() {
        return curTemp;
    }

    public void setCurTemp(String curTemp) {
        this.curTemp = curTemp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getPhotoRef() {
        return photoRef;
    }

    public void setPhotoRef(String photoRef) {
        this.photoRef = photoRef;
    }
}
