package com.fandev.fantasticweatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.fandev.fantasticweatherapp.controller.AppController;
import com.fandev.fantasticweatherapp.data.CurrentAdapter;
import com.fandev.fantasticweatherapp.data.CurrentWeatherResponse;
import com.fandev.fantasticweatherapp.data.ForecastViewPagerAdapter;
import com.fandev.fantasticweatherapp.data.PhotoAsyncResponse;
import com.fandev.fantasticweatherapp.data.SimpleItemTouchHelperCallback;
import com.fandev.fantasticweatherapp.model.Current;
import com.fandev.fantasticweatherapp.service.AutoUpdateService;
import com.fandev.fantasticweatherapp.util.Prefs;
import com.fandev.fantasticweatherapp.util.Time;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private PopupWindow popupWindow;
    private FloatingActionButton addWeatherButton;
    private TextInputLayout textInputCustomIcon;
    private TextInputEditText textInputEditText;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView currentWeatherRecyclerView;
    private ArrayList<Current> currentWeatherArrayList;
    private CurrentAdapter currentAdapter;
    private ImageView bingPic;
    private FloatingActionButton backButton;
    private Time time = new Time();
    private ItemTouchHelper itemTouchHelper;
    private String formalCityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        addWeatherButton = findViewById(R.id.add_weather_button);
        bingPic = findViewById(R.id.bing_pic);
        coordinatorLayout = findViewById(R.id.container);
        currentWeatherRecyclerView = findViewById(R.id.current_weather_recycler_view);
        currentWeatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // if user has added some cities before, then show the current weather of those cities
        if (intent.getSerializableExtra("cityList") != null) {
            Log.d(TAG, "get cityList!");
            currentWeatherArrayList = (ArrayList<Current>) (intent.getSerializableExtra("cityList"));
        } else {
            currentWeatherArrayList = new ArrayList<>();
        }
        currentAdapter = new CurrentAdapter(this, currentWeatherArrayList);
        currentWeatherRecyclerView.setAdapter(currentAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(currentAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(currentWeatherRecyclerView);
        currentAdapter.notifyDataSetChanged();
        showIntro("Welcome to Fantastic Weather App!\n\n" +
                "Let me give you a quick overview of how it works.\n\n" +
                "First, press the button at the bottom right to add the city you want to check its weather.",
                addWeatherButton, "add_weather_button_showcase");

        addWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popup_window, (ViewGroup) findViewById(R.id.container), false);
                popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
                popupWindow.showAtLocation(coordinatorLayout, Gravity.CENTER, 0, 0);
                textInputCustomIcon = customView.findViewById(R.id.search_icon);
                textInputEditText = customView.findViewById(R.id.location_text);
                backButton = customView.findViewById(R.id.back_button_in_popup_window);
                addWeatherButton.setVisibility(View.GONE);
                final String[] photoReference = {""};

                textInputCustomIcon.setEndIconOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editable editText = textInputEditText.getText();
                        if (editText != null) {
                            final String location = editText.toString().trim();
                            if (!TextUtils.isEmpty(location)) {
                                getCurrentWeather(new CurrentWeatherResponse() {
                                    @Override
                                    public void processFinished() {
                                        getPhotoByPlace(new PhotoAsyncResponse() {
                                            @Override
                                            public void processFinished(String photoUrl) {
                                                Log.d(TAG, "processFinished: " + photoUrl);
                                                photoReference[0] = photoUrl;
                                                for (Current currentWeatherItem: currentWeatherArrayList) {
                                                    Log.d(TAG, "city name: " + currentWeatherItem.getCityName().toLowerCase() + ", " + location.toLowerCase());
                                                    if (TextUtils.equals(currentWeatherItem.getCityName().toLowerCase(), formalCityName.toLowerCase())) {
                                                        Log.d(TAG, "added photo reference");
                                                        currentWeatherItem.setPhotoRef(photoUrl);
                                                    }
                                                }
                                                Prefs prefs = new Prefs(MainActivity.this);
                                                prefs.setCityList(currentWeatherArrayList);
                                                popupWindow.dismiss();
                                                currentAdapter.notifyDataSetChanged();
                                                addWeatherButton.setVisibility(View.VISIBLE);

                                                Log.d(TAG, "photoReference: " + photoReference[0]);
                                            }
                                        }, formalCityName);
                                    }
                                }, location);
                            } else {
                                textInputCustomIcon.setError("Your input does not give a match");
                            }
                        }
                    }
                });
                textInputEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        textInputCustomIcon.setError(null);
                    }
                    @Override
                    public void afterTextChanged(Editable s) { }
                });
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        addWeatherButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        Prefs prefs = new Prefs(this);
        String bingPicUrl = prefs.getBingPic();
        // if the url of the bing picture has been cached, load it directly
        //  Otherwise, fetch bing picture from the Internet
        if (bingPicUrl != null) {
            Glide.with(getApplicationContext()).load(bingPicUrl).into(bingPic);
            Log.d(TAG, "Fetched bing pic from cache");
        } else {
            getBingPic();
        }
        // if cities have been cached, load it directly
        if (prefs.getCityList() != null) {
            currentWeatherArrayList.clear();
            currentWeatherArrayList.addAll(prefs.getCityList());
            for (Current item: currentWeatherArrayList) {
                updateWeatherItem(item);
            }
            Log.d(TAG, "Fetched city list from cache");
        }
        // start the auto update service
        Intent serviceIntent = new Intent(this, AutoUpdateService.class);
        startService(serviceIntent);
    }

    /**
     * Get the current weather info from the location that the user gives.
     *
     * @param callback
     *          a callback function that will be called after getting the current weather info
     * @param locationText
     *          the city name that user provides
     */
    private void getCurrentWeather(final CurrentWeatherResponse callback, final String locationText) {
        String queryUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + locationText
                + "&units=metric&APPID=" + BuildConfig.WEATHER_API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                queryUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                try {
                    JSONObject mainObject = response.getJSONObject("main");
                    JSONObject weatherObject = response.getJSONArray("weather").getJSONObject(0);
                    JSONObject windObject = response.getJSONObject("wind");
                    JSONObject sysObject = response.getJSONObject("sys");

                    String cityName = response.getString("name");
                    formalCityName = cityName;
                    long dateUTC = response.getLong("dt");
                    String date = time.getDateFromUTC(dateUTC, "EE, HH:mm a, z");
                    // inside sys object
                    long sunriseTimeUTC = sysObject.getLong("sunrise");
                    long sunsetTimeUTC = sysObject.getLong("sunset");
                    String countryName = sysObject.getString("country");
                    String sunriseTime = time.getDateFromUTC(sunriseTimeUTC, "yyyy-MM-dd HH:mm:ss");
                    String sunsetTime = time.getDateFromUTC(sunsetTimeUTC, "yyyy-MM-dd HH:mm:ss");
                    // check if the city has been added
                    if (checkDuplicateCityInput(cityName, currentWeatherArrayList)){
                        textInputCustomIcon.setError("Your already added this city");
                        return;
                    }
                    // inside wind object
                    String windSpeed = windObject.getString("speed");
                    // inside main object
                    String currentTemp = Math.round(Double.parseDouble(mainObject.getString("temp"))) + " \u2103";
                    Log.d(TAG, "currentTemp: " + currentTemp);
//                    String lowTemp = mainObject.getString("temp_min");
//                    String highTemp = mainObject.getString("temp_max");
                    String humidity = mainObject.getString("humidity");
                    // inside weather object
                    String currentDescription = weatherObject.getString("description");
                    String icon = weatherObject.getString("icon");
                    // set fields of a current weather class of this city
                    Current currentWeatherItem = new Current();
                    currentWeatherItem.setCityName(cityName);
                    currentWeatherItem.setDate(date);
                    currentWeatherItem.setCountryName(countryName);
                    currentWeatherItem.setSpeed(windSpeed);
                    currentWeatherItem.setCurTemp(currentTemp);
                    currentWeatherItem.setDescription(currentDescription);
                    currentWeatherItem.setIcon(icon);
                    currentWeatherItem.setHumidity(humidity);
                    currentWeatherArrayList.add(currentWeatherItem);
                    Log.d(TAG, "onClick: " + currentWeatherArrayList.size());
                } catch (JSONException error) {
                    error.printStackTrace();
                    textInputCustomIcon.setError("Your input does not give a match");
                }
                if (null != callback) callback.processFinished();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                textInputCustomIcon.setError("Your input does not give a match");
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Check if the user provides duplicate input (the city has been added)
     * @param cityName
     *          the city name that the user provides
     * @param currentWeatherArrayList
     *          a list of current weather info of cities provided by the user
     * @return
     *          if the user provides duplicate input (the city has been added)
     */
    private boolean checkDuplicateCityInput(String cityName, ArrayList<Current> currentWeatherArrayList) {
        if (currentWeatherArrayList == null || currentWeatherArrayList.size() == 0) return false;
        for (Current current: currentWeatherArrayList) {
            if (TextUtils.equals(cityName, current.getCityName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fetch Bing daily picture and set it as the background image of app
     */
    private void getBingPic() {
        String queryUrl = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                queryUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String imageUrl = "https://www.bing.com" +
                            response.getJSONArray("images").getJSONObject(0).getString("url");
                    Glide.with(getApplicationContext()).load(imageUrl).into(bingPic);
                    Prefs prefs = new Prefs(MainActivity.this);
                    prefs.setBingPic(imageUrl);
                    Log.d(TAG, "Bing image loads successfully!");
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

    /**
     * Fetch the url of a photo of the city by Google Place API,
     *  and set it as the "photo_reference" field of current weather info class of that city.
     * @param callback
     *          a callback function that will be called after getting the photo url
     * @param cityName
     *          a city name that the user provides
     */
    private void getPhotoByPlace (final PhotoAsyncResponse callback, String cityName) {
        String queryRequest = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input="
                + cityName + "+city&inputtype=textquery&fields=photos&key=" + BuildConfig.PHOTO_API_KEY;
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
                if (null != callback) callback.processFinished(photoUrl);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Update the weather info of a city
     * @param currentWeatherItem
     *          a class containing current weather info of a city
     */
    private void updateWeatherItem(final Current currentWeatherItem) {
        String queryUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + currentWeatherItem.getCityName()
                + "&units=metric&APPID=" + BuildConfig.WEATHER_API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                queryUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                try {
                    JSONObject mainObject = response.getJSONObject("main");
                    JSONObject weatherObject = response.getJSONArray("weather").getJSONObject(0);

                    String cityName = response.getString("name");
                    String currentTemp = Math.round(Double.parseDouble(mainObject.getString("temp"))) + " \u2103";
                    Log.d(TAG, "currentTemp: " + currentTemp);
                    String currentDescription = weatherObject.getString("description");

                    currentWeatherItem.setCityName(cityName);
                    currentWeatherItem.setCurTemp(currentTemp);
                    currentWeatherItem.setDescription(currentDescription);
//                    currentWeatherItem.setPhotoRef(currentWeatherItem.getPhotoRef());

                    Prefs prefs = new Prefs(MainActivity.this);
                    prefs.setCityList(currentWeatherArrayList);
                    Log.d(TAG, "updated: " + currentWeatherArrayList.size());
                    Log.d(TAG, "Current weather item updates successfully!");
                    currentAdapter.notifyDataSetChanged();
                } catch (JSONException error) {
                    error.printStackTrace();
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

    /**
     * Show introduction text for a first-time user
     * @param text
     *          the introduction text
     * @param view
     *          the view that will be introduced
     * @param showCaseID
     *          a unique ID used to ensure it is only shown once
     */
    private void showIntro(String text, View view, String showCaseID) {
        new MaterialShowcaseView.Builder(this)
                .setTarget(view)
                .setContentText(text)
                .setDismissOnTouch(true)
                .setDismissOnTargetTouch(true)
                .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(showCaseID) // provide a unique ID used to ensure it is only shown once
                .show();
    }
}
