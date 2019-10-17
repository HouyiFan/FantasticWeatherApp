package com.fandev.fantasticweatherapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fandev.fantasticweatherapp.controller.AppController;
import com.fandev.fantasticweatherapp.model.Current;
import com.fandev.fantasticweatherapp.util.Prefs;
import com.fandev.fantasticweatherapp.util.Time;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * Auto update service class that will update Bing picture every 6 hours
 */
public class AutoUpdateService extends Service {

    private static final String TAG = "AutoUpdateService";
    private Time time = new Time();
    private ArrayList<Current> currentWeatherArrayList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateBingPic();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int sixHours = 6 * 60 * 60 * 1000; //6 hours
        long triggerAtTime = SystemClock.elapsedRealtime() + sixHours;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
//        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Fetch updated Bing picture
     */
    private void updateBingPic(){
        String queryUrl = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                queryUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String imageUrl = "https://www.bing.com" +
                            response.getJSONArray("images").getJSONObject(0).getString("url");
                    Prefs prefs = new Prefs(AutoUpdateService.this);
                    prefs.setBingPic(imageUrl);
                    Log.d(TAG, "Bing image updates successfully!");
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

}
