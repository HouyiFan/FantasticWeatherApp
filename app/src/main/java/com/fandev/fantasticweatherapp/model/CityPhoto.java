package com.fandev.fantasticweatherapp.model;

import java.io.Serializable;

public class CityPhoto implements Serializable {

    private String cityPhotoUrl;

    public String getCityPhotoUrl() {
        return cityPhotoUrl;
    }

    public void setCityPhotoUrl(String cityPhotoUrl) {
        this.cityPhotoUrl = cityPhotoUrl;
    }
}
