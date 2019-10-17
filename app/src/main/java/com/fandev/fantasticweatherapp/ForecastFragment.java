package com.fandev.fantasticweatherapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fandev.fantasticweatherapp.data.ForecastHourlyAdapter;
import com.fandev.fantasticweatherapp.model.DailyForecast;
import com.fandev.fantasticweatherapp.model.HourlyForecast;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForecastFragment extends Fragment {

    private static final String TAG = "ForecastFragment";

    private ArrayList<HourlyForecast> hourlyForecastList;

    public ForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ForecastFragment.
     */
    public static ForecastFragment newInstance(DailyForecast forecast) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable("forecast", forecast);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View forecastView = inflater.inflate(R.layout.fragment_forecast, container, false);

        ImageView forecastIcon = forecastView.findViewById(R.id.forecast_daily_weather_icon);
        TextView forecastCity = forecastView.findViewById(R.id.forecast_city_name);
        TextView forecastDate =forecastView.findViewById(R.id.forecast_date);
        TextView forecastHigh = forecastView.findViewById(R.id.forecast_high_temp);
        TextView forecastLow = forecastView.findViewById(R.id.forecast_low_temp);
        TextView forecastDescription = forecastView.findViewById(R.id.forecast_description);
        TextView forecastHumidity = forecastView.findViewById(R.id.forecast_humidity);
        TextView forecastWindSpeed = forecastView.findViewById(R.id.forecast_wind_speed);
        ImageView cityPhoto = forecastView.findViewById(R.id.city_photo);
        RecyclerView forecastHourlyRecyclerView = forecastView.findViewById(R.id.forecast_hourly_recyclerview);
        forecastHourlyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        hourlyForecastList = new ArrayList<>();
        ForecastHourlyAdapter forecastHourlyAdapter = new ForecastHourlyAdapter(getActivity(), hourlyForecastList);
        forecastHourlyRecyclerView.setAdapter(forecastHourlyAdapter);

        DailyForecast dailyForecast = (DailyForecast) getArguments().getSerializable("forecast");
        List<HourlyForecast> hourlyForecastListAtDayPos = dailyForecast.getList();
        double lowTemp = Double.MAX_VALUE;
        double highTemp = Double.MIN_VALUE;
        double dayTemp = 0;

        for (HourlyForecast hourlyForecast: hourlyForecastListAtDayPos) {
//            Double highTempHourly = Double.parseDouble(hourlyForecast.getHighTemp());
//            Double lowTempHourly = Double.parseDouble(hourlyForecast.getLowTemp());
            Double tempHourly = Double.parseDouble(hourlyForecast.getTemp());
//            highTemp = Math.max(highTempHourly, highTemp);
//            lowTemp = Math.min(lowTempHourly, lowTemp);
            highTemp = Math.max(highTemp, tempHourly);
            lowTemp = Math.min(lowTemp, tempHourly);
            dayTemp += tempHourly;
        }
        dayTemp /= hourlyForecastListAtDayPos.size();
        hourlyForecastList.clear();
        hourlyForecastList.addAll(hourlyForecastListAtDayPos);
        forecastHourlyAdapter.notifyDataSetChanged();

        String detailTime = hourlyForecastListAtDayPos.get(0).getDetailTime();
        String weatherDescription = hourlyForecastListAtDayPos.get(0).getWeatherDescription();
        String forecastHighTemp = Math.round(highTemp) + " \u2103";
        String forecastLowTemp = Math.round(lowTemp) + " \u2103";
        String forecastHumidityText = hourlyForecastListAtDayPos.get(0).getHumidity() + "% humidity";
        String forecastWindSpeedText = Math.round(Double.parseDouble(hourlyForecastListAtDayPos.get(0).getWindSpeed())) + " km/h Winds";
        String forecastWeatherIconUri = "http://openweathermap.org/img/wn/" + hourlyForecastListAtDayPos.get(0).getWeatherIcon()
                                        + ".png";
        Log.d(TAG, "onCreateView: " + forecastWeatherIconUri);
        String cityPhotoUri = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&photoreference="
                                    + dailyForecast.getCityPhoto() + "&key=" + BuildConfig.PHOTO_API_KEY;
        Log.d(TAG, "cityPhotoUri: " + cityPhotoUri);

        forecastCity.setText(dailyForecast.getCityName());
        forecastDate.setText(detailTime.substring(0, detailTime.indexOf(" ")));
        forecastHigh.setText(forecastHighTemp);
        forecastLow.setText(forecastLowTemp);
        forecastDescription.setText(weatherDescription);
        forecastHumidity.setText(forecastHumidityText);
        forecastWindSpeed.setText(forecastWindSpeedText);
        Glide.with(forecastView).load(forecastWeatherIconUri).
                apply(new RequestOptions().override(600, 200)).into(forecastIcon);
        Glide.with(forecastView).load(cityPhotoUri).into(cityPhoto);

        MaterialCardView cardView = forecastView.findViewById(R.id.card_view);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity());
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                                    .setTarget(cardView)
                                    .setContentText("Here is the detailed weather info.")
                                    .singleUse("cardview_showcase1")
                                    .setDismissOnTouch(true)
                                    .setDismissOnTargetTouch(true)
                                    .setDelay(800)
                                    .build());
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                                    .setTarget(forecastHourlyRecyclerView)
                                    .setContentText("You can scroll down to view the weather of each 3 hours of this day.")
                                    .singleUse("recyclerview_forecast_hourly")
                                    .setDismissOnTouch(true)
                                    .setDismissOnTargetTouch(true)
                                    .setDelay(600)
                                    .build());
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                                    .setTarget(cardView)
                                    .setContentText("And you can swipe this page to jump to another day's weather. Enjoy it!")
                                    .singleUse("cardview_showcase2")
                                    .setDismissOnTouch(true)
                                    .setDismissOnTargetTouch(true)
                                    .setGravity(Gravity.BOTTOM)
                                    .setDelay(600)
                                    .build());
        sequence.start();
        return forecastView;
    }

}
