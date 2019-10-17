package com.fandev.fantasticweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fandev.fantasticweatherapp.controller.AppController;
import com.fandev.fantasticweatherapp.data.ForecastHourlyAdapter;
import com.fandev.fantasticweatherapp.data.ForecastListAsyncResponse;
import com.fandev.fantasticweatherapp.data.ForecastViewPagerAdapter;
import com.fandev.fantasticweatherapp.data.PhotoAsyncResponse;
import com.fandev.fantasticweatherapp.model.Current;
import com.fandev.fantasticweatherapp.model.DailyForecast;
import com.fandev.fantasticweatherapp.model.Forecast;
import com.fandev.fantasticweatherapp.model.HourlyForecast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends AppCompatActivity {

    private static final String TAG = "ForecastActivity";

    private ViewPager viewPager;
    private ForecastViewPagerAdapter forecastViewPagerAdapter;
    private FloatingActionButton backButton;
    private ArrayList<DailyForecast> dailyForecastArrayList;
    private ArrayList<Current> currentCityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forecast);

        viewPager = findViewById(R.id.view_pager);
        backButton = findViewById(R.id.back_button);
        dailyForecastArrayList = new ArrayList<>();

        // Fetch the city name and the photo url of the city selected by the user
        Intent intent = getIntent();
        String location = intent.getStringExtra("selectedCityName");
        String photoRef = intent.getStringExtra("selectedPhotoRef");
        currentCityList = (ArrayList<Current>) intent.getSerializableExtra("cityList");
        Log.d(TAG, "photoRef: " + photoRef);
        Log.d(TAG, "currentCityList: " + currentCityList.get(0).getCityName());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                if (currentCityList != null) {
                    Log.d(TAG, "back button -> currentCityList: " + currentCityList.get(0).getCityName());
                    i.putExtra("cityList", currentCityList);
                }
                startActivity(i);
                finish();
            }
        });

        getForecastWeather(location, photoRef);
    }

    /**
     * Fetch the upcoming 5 day's weather info, set daily weather fragment inside a view pager
     * @param location
     *          the city name
     * @param photoRef
     *          a photo url of the city
     */
    public void getForecastWeather(String location, String photoRef) {
        forecastViewPagerAdapter = new ForecastViewPagerAdapter(this, getSupportFragmentManager(),
                getFragments(location, photoRef));
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(forecastViewPagerAdapter);
    }

    /**
     * Set daily weather's fragment
     * @param location
     *          the city name
     * @param photoRef
     *          a photo url of the city
     * @return
     *          a list of fragment that will be used by the view pager
     */
    private List<Fragment> getFragments(String location, final String photoRef){
        final List<Fragment> fragmentList = new ArrayList<>();

        getForecast(new ForecastListAsyncResponse() {
            @Override
            public void processFinished(List<DailyForecast> dailyForecastArrayList) {
                fragmentList.clear();
                for (int i = 0; i < dailyForecastArrayList.size(); i++) {
                    DailyForecast dailyForecast = dailyForecastArrayList.get(i);
                    dailyForecast.setCityPhoto(photoRef);
                    ForecastFragment forecastFragment = ForecastFragment.newInstance(dailyForecast);
                    fragmentList.add(forecastFragment);
                }
                forecastViewPagerAdapter.notifyDataSetChanged();
            }
        }, location);
        return fragmentList;
    }

    /**
     * Fetch the upcoming 120 hours' weather info with 3 hours interval (40 in total),
     *  set them as HourlyForecast objects and add them to the corresponding DailyForecast objects
     * @param callback
     *          a callback function that will be called after tasks finish
     * @param locationText
     *          the city name
     */
    private void getForecast( final ForecastListAsyncResponse callback, String locationText) {
        String queryUrl = "http://api.openweathermap.org/data/2.5/forecast?q=" + locationText
                + "&units=metric&APPID=" + BuildConfig.WEATHER_API_KEY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                queryUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String code = response.getString("cod");
                    int lines = response.getInt("cnt");
                    Log.d(TAG, "Line Number: " + lines);

                    JSONObject cityObject = response.getJSONObject("city");
                    JSONArray listObjectArray = response.getJSONArray("list");

                    final String cityName = cityObject.getString("name");
                    String countryName = cityObject.getString("country");

                    Log.d(TAG, "cityName: " + cityName);
                    Log.d(TAG, "countryName: " + countryName);

                    final Forecast forecast = new Forecast();
                    forecast.setCityName(cityName);
                    forecast.setCountryName(countryName);
                    forecast.setList(new ArrayList<DailyForecast>());

                    DailyForecast dailyForecast = new DailyForecast();
                    dailyForecast.setCityName(cityName);
                    dailyForecast.setList(new ArrayList<HourlyForecast>());
                    String curDetailTime = listObjectArray.getJSONObject(0).getString("dt_txt");
                    String curDetailDate = curDetailTime.substring(0, curDetailTime.indexOf(" "));
                    Log.d(TAG, "curDetailTime: " + curDetailTime);
                    Log.d(TAG, "curDetailDate: " + curDetailDate);
                    // Iterate all hourly forecast objects
                    for (int i = 0; i < lines; i++) {
                        JSONObject forecastObject = listObjectArray.getJSONObject(i);
                        JSONObject mainObject = forecastObject.getJSONObject("main");
                        JSONObject weatherObject = forecastObject.getJSONArray("weather").getJSONObject(0);
                        JSONObject cloudObject = forecastObject.getJSONObject("clouds");
                        JSONObject windObject = forecastObject.getJSONObject("wind");

                        String date = forecastObject.getString("dt");
                        String detailTime = forecastObject.getString("dt_txt");
                        String temp = mainObject.getString("temp");
                        String lowTemp = mainObject.getString("temp_min");
                        String highTemp = mainObject.getString("temp_max");
                        String airPressure = mainObject.getString("pressure");
                        String humidity = mainObject.getString("humidity");
                        String weatherCond = weatherObject.getString("main");
                        String weatherDescription = weatherObject.getString("description");
                        String weatherIcon = weatherObject.getString("icon");
                        String cloudPercent = cloudObject.getString("all");
                        String windSpeed = windObject.getString("speed");

                        String detailDate = detailTime.substring(0, detailTime.indexOf(" "));
                        // set HourlyForecast object
                        HourlyForecast hourlyForecast = new HourlyForecast();
                        hourlyForecast.setDate(date);
                        hourlyForecast.setDetailTime(detailTime);
                        hourlyForecast.setTemp(temp);
                        hourlyForecast.setLowTemp(lowTemp);
                        hourlyForecast.setHighTemp(highTemp);
                        hourlyForecast.setAtmospherePressure(airPressure);
                        hourlyForecast.setHumidity(humidity);
                        hourlyForecast.setWeatherCondition(weatherCond);
                        hourlyForecast.setWeatherDescription(weatherDescription);
                        hourlyForecast.setWeatherIcon(weatherIcon);
                        hourlyForecast.setCloudiness(cloudPercent);
                        hourlyForecast.setWindSpeed(windSpeed);
                        // add HourlyForecast object to the correct corresponding DailyForecast object
                        if (!TextUtils.equals(detailDate, curDetailDate)) {
                            forecast.getList().add(dailyForecast);
                            dailyForecast = new DailyForecast();
                            dailyForecast.setCityName(cityName);
                            dailyForecast.setList(new ArrayList<HourlyForecast>());
                            dailyForecast.getList().add(hourlyForecast);
                            curDetailDate = detailDate;
                        } else {
                            dailyForecast.getList().add(hourlyForecast);
                        }
                    }
                    forecast.getList().add(dailyForecast);
                    dailyForecastArrayList = new ArrayList<>(forecast.getList());
                    forecastViewPagerAdapter.notifyDataSetChanged();
                } catch (JSONException error) {
                    error.printStackTrace();
                }
                if (null != callback) {
                    callback.processFinished(dailyForecastArrayList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
