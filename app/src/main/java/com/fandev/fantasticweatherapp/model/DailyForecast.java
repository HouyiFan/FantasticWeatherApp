package com.fandev.fantasticweatherapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DailyForecast implements Serializable {

    private List<HourlyForecast> list;
    private String cityName;
    private String cityPhoto;

    public DailyForecast() {
        this.list = new ArrayList<>();
    }

    public List<HourlyForecast> getList() {
        return list;
    }

    public void setList(List<HourlyForecast> list) {
        this.list = list;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityPhoto() {
        return cityPhoto;
    }

    public void setCityPhoto(String cityPhoto) {
        this.cityPhoto = cityPhoto;
    }
}
