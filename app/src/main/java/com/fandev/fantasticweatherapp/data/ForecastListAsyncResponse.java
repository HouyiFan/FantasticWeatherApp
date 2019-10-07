package com.fandev.fantasticweatherapp.data;

import com.fandev.fantasticweatherapp.model.DailyForecast;

import java.util.List;

public interface ForecastListAsyncResponse {
    void processFinished(List<DailyForecast> forecastArrayList);
}
