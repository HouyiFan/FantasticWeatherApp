package com.fandev.fantasticweatherapp.model;

import java.io.Serializable;

public class HourlyForecast implements Serializable {

    // under "list" attribute
    private String date;
    private String detailTime;

    // under "list" -> "main"
    private String temp;
    private String highTemp;
    private String lowTemp;
    private String atmospherePressure;
    private String humidity;

    // under "list" -> "weather"
    private String weatherCondition;
    private String weatherDescription;
    private String weatherIcon;

    // under "list" -> "clouds"
    private String cloudiness;

    // under "list" -> "wind"
    private String windSpeed;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetailTime() {
        return detailTime;
    }

    public void setDetailTime(String detailTime) {
        this.detailTime = detailTime;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHighTemp() {
        return highTemp;
    }

    public void setHighTemp(String highTemp) {
        this.highTemp = highTemp;
    }

    public String getLowTemp() {
        return lowTemp;
    }

    public void setLowTemp(String lowTemp) {
        this.lowTemp = lowTemp;
    }

    public String getAtmospherePressure() {
        return atmospherePressure;
    }

    public void setAtmospherePressure(String atmospherePressure) {
        this.atmospherePressure = atmospherePressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getCloudiness() {
        return cloudiness;
    }

    public void setCloudiness(String cloudiness) {
        this.cloudiness = cloudiness;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

}
