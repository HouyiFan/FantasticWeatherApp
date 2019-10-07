package com.fandev.fantasticweatherapp.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.fandev.fantasticweatherapp.model.Current;

import java.util.ArrayList;
import java.util.List;

public class Prefs {
    private SharedPreferences preferences;

    public Prefs(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

//    public Prefs(Context context) {
//        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
//    }

    public void setBingPic(String location){
        preferences.edit().putString("bing_pic", location).apply();
    }

    public String getBingPic(){
        return preferences.getString("bing_pic", null);
    }

    public void setCityList(ArrayList<Current> cityList){
        SharedPreferences.Editor editor = preferences.edit();
        try {
            editor.putString("city_list", ObjectSerializer.serialize(cityList)).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Current> getCityList(){
        List<Current> res = new ArrayList<>();
        String cityList = preferences.getString("city_list", null);
        try {
             res = (ArrayList<Current>)ObjectSerializer.deserialize(cityList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public boolean checkFirstTimeUser() {
        return preferences.getBoolean("first_time_user", false);
    }

    public void updateFirstTimeUser() {
        preferences.edit().putBoolean("first_time_user", true).apply();
    }

}
