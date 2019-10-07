package com.fandev.fantasticweatherapp.model;

import java.io.Serializable;
import java.util.List;

// A class used to store OpenWeatherAPI response data
public class Forecast implements Serializable {

    // under "city" attribute
    private String cityName;
    private String countryName;

    private List<DailyForecast> list;

    private String cityPhoto;

//    // under "list" attribute
//    private String date;
//    private String detailTime;
//
//    // under "list" -> "main"
//    private String temp;
//    private String highTemp;
//    private String lowTemp;
//    private String atmospherePressure;
//    private String humidity;
//
//    // under "list" -> "weather"
//    private String weatherCondition;
//    private String weatherDescription;
//    private String weatherIcon;
//
//    // under "list" -> "clouds"
//    private String cloudiness;
//
//    // under "list" -> "wind"
//    private String windSpeed;
//    private String windDegree;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public List<DailyForecast> getList() {
        return list;
    }

    public void setList(List<DailyForecast> list) {
        this.list = list;
    }

    public String getCityPhoto() {
        return cityPhoto;
    }

    public void setCityPhoto(String cityPhoto) {
        this.cityPhoto = cityPhoto;
    }
}
