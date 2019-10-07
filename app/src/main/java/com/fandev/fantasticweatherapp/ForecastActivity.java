package com.fandev.fantasticweatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import android.app.Activity;
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
import com.android.volley.toolbox.RequestFuture;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ForecastActivity extends AppCompatActivity {

    private static final String TAG = "ForecastActivity";

    private ViewPager viewPager;
    private ForecastViewPagerAdapter forecastViewPagerAdapter;
    private FloatingActionButton backButton;
    private ArrayList<DailyForecast> dailyForecastArrayList;
    private ForecastHourlyAdapter forecastHourlyAdapter;
    private ArrayList<Current> currentCityList;
//    private RecyclerView forecastWeatherRecyclerView;
//    private ArrayList<HourlyForecast> hourlyForecastArrayList;
//    ForecastHourlyAdapter forecastHourlyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_forecast);

        viewPager = findViewById(R.id.view_pager);
        backButton = findViewById(R.id.back_button);
        dailyForecastArrayList = new ArrayList<>();
//        hourlyForecastArrayList = new ArrayList<>();
//        forecastWeatherRecyclerView = findViewById(R.id.forecast_hourly_recyclerview);
//        forecastWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        forecastHourlyAdapter = new ForecastHourlyAdapter(this, hourlyForecastArrayList);

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

//        showIntro("Here is the detailed weather info of today",
//                viewPager,
//                "daily_forecast_showcase");

//        ShowcaseConfig config = new ShowcaseConfig();
//        config.setDelay(500); // half second between each showcase view
//        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "forecast_weather_showcase");
//        sequence.setConfig(config);
//        sequence.addSequenceItem(viewPager.getChildAt(0).findViewById(R.id.card_view),
//                "Here is the detailed weather info.", "GOT IT");
//        sequence.addSequenceItem(viewPager.getChildAt(0).findViewById(R.id.forecast_hourly_recyclerview),
//                "You can scroll down to view the weather of each 3 hours of this day.", "GOT IT");
//        sequence.addSequenceItem(viewPager.getChildAt(0).findViewById(R.id.card_view),
//                "And you can swipe it to check another day's weather. Enjoy it!", "GOT IT");
//        sequence.start();
    }

    public void getForecastWeather(String location, String photoRef) {
        forecastViewPagerAdapter = new ForecastViewPagerAdapter(this, getSupportFragmentManager(),
                getFragments(location, photoRef));
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(forecastViewPagerAdapter);
    }

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
//                    if (!TextUtils.equals("200", code)) {
//                        return;
//                    }
                    JSONObject cityObject = response.getJSONObject("city");
                    JSONArray listObjectArray = response.getJSONArray("list");

                    final String cityName = cityObject.getString("name");
                    String countryName = cityObject.getString("country");

//                    final List<String> photoUrl = new ArrayList<>();
//                    getPhotoByPlace(new PhotoAsyncResponse() {
//                        @Override
//                        public void processFinished(String url) {
//                            photoUrl.add(url);
//                        }
//                    }, cityName);
//                    getPhotoByPlace(cityName);

                    Log.d(TAG, "cityName: " + cityName);
                    Log.d(TAG, "countryName: " + countryName);


                    final Forecast forecast = new Forecast();
                    forecast.setCityName(cityName);
                    forecast.setCountryName(countryName);
                    forecast.setList(new ArrayList<DailyForecast>());
//                    forecast.setCityPhoto(photoUrl.get(0));
//                    getPhotoByPlace(cityName, forecast);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            getPhotoByPlace(cityName, forecast);
//                            forecastViewPagerAdapter.notifyDataSetChanged();
//                        }
//                    });
//                    RequestFuture<JSONObject> future = RequestFuture.newFuture();
//                    JsonObjectRequest request = new JsonObjectRequest("https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="
//                            + cityName + "&inputtype=textquery&fields=photos&key=" + BuildConfig.PHOTO_API_KEY, new JSONObject(), future, future);
//                    AppController.getInstance().getRequestQueue().add(request);
//
//                    try {
//                        JSONObject res = future.get(); // this will block
//                    } catch (Exception e) {
//                        // exception handling
//                        e.printStackTrace();
//                    }

                    DailyForecast dailyForecast = new DailyForecast();
                    dailyForecast.setCityName(cityName);
//                    dailyForecast.setCityPhoto(forecast.getCityPhoto());
                    dailyForecast.setList(new ArrayList<HourlyForecast>());
                    String curDetailTime = listObjectArray.getJSONObject(0).getString("dt_txt");
                    String curDetailDate = curDetailTime.substring(0, curDetailTime.indexOf(" "));

                    Log.d(TAG, "curDetailTime: " + curDetailTime);
                    Log.d(TAG, "curDetailDate: " + curDetailDate);


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

                        if (!TextUtils.equals(detailDate, curDetailDate)) {
                            forecast.getList().add(dailyForecast);
                            dailyForecast = new DailyForecast();
                            dailyForecast.setCityName(cityName);
//                            dailyForecast.setCityPhoto(forecast.getCityPhoto());
//                            dailyForecast.setCityPhoto(photoUrl.get(0));
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

    private void getPhotoByPlace (final PhotoAsyncResponse callback, String cityName) {
        String queryRequest = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="
                + cityName + "&inputtype=textquery&fields=photos&key=" + BuildConfig.PHOTO_API_KEY;
        Log.d(TAG, "getPhotoByPlace: " + queryRequest);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                queryRequest, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String photoUrl = "";
                try {
                    photoUrl = response.getJSONArray("candidates").getJSONObject(0)
                                    .getJSONArray("photos").getJSONObject(0)
                                    .getString("photo_reference");
                    Log.d(TAG, "onResponse: " + photoUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (null != callback) {
                    callback.processFinished(photoUrl);
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

    private void getPhotoByPlace (String cityName, final Forecast forecast) {
        String queryRequest = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="
                + cityName + "&inputtype=textquery&fields=photos&key=" + BuildConfig.PHOTO_API_KEY;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                queryRequest, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String photoUrl = "";
                try {
                    photoUrl = response.getJSONArray("candidates").getJSONObject(0)
                            .getJSONArray("photos").getJSONObject(0)
                            .getString("photo_reference");
                    forecast.setCityPhoto(photoUrl);
                    Log.d(TAG, "onResponse: " + photoUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void showIntro(String text, View view, String showCaseID) {
        new MaterialShowcaseView.Builder(this)
                .setTarget(view)
                .setDismissText("GOT IT")
                .setContentText(text)
                .setDelay(2000) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(showCaseID) // provide a unique ID used to ensure it is only shown once
                .show();
    }
}
