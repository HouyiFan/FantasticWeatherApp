package com.fandev.fantasticweatherapp.data;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fandev.fantasticweatherapp.R;
import com.fandev.fantasticweatherapp.model.HourlyForecast;
import com.fandev.fantasticweatherapp.util.Time;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ForecastHourlyAdapter extends RecyclerView.Adapter<ForecastHourlyAdapter.ForecastHourlyHolder> {
    private static final String TAG = "ForecastHourlyAdapter";

    private Context context;
    private ArrayList<HourlyForecast> hourlyForecasts;
    private ViewGroup viewGroup;

    public ForecastHourlyAdapter(Context context, ArrayList<HourlyForecast> hourlyForecasts) {
        this.context = context;
        this.hourlyForecasts = hourlyForecasts;
    }

    public static class ForecastHourlyHolder extends RecyclerView.ViewHolder {
        private ImageView weatherIcon;
        private TextView time, temp, condition;

        public ForecastHourlyHolder(@NonNull View itemView) {
            super(itemView);
            weatherIcon = itemView.findViewById(R.id.forecast_hourly_image);
            time = itemView.findViewById(R.id.forecast_hourly_detail_time);
            temp = itemView.findViewById(R.id.forecast_hourly_temp);
            condition = itemView.findViewById(R.id.forecast_hourly_weather_condition);
        }

        public void setDetails(HourlyForecast hourlyForecast, Context context) {
            weatherIcon.setImageURI(null);
            String imageUri = "http://openweathermap.org/img/wn/" + hourlyForecast.getWeatherIcon() + ".png";
//            try {
//                URL imageUrl = new URL(imageUri);
//                Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection() .getInputStream());
//                weatherIcon.setImageBitmap(bitmap);
//                Log.d(TAG, "setDetails: " + imageUri);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            weatherIcon.setImageURI(Uri.parse(imageUri));
            Glide.with(context).load(imageUri).into(weatherIcon);
            String detailTime = hourlyForecast.getDetailTime();
            String tempText = Math.round(Double.parseDouble(hourlyForecast.getTemp())) + " \u2103";
            time.setText(new Time().parseDate(detailTime, "yyyy-MM-dd HH:mm:ss", "hh:mm a"));
            temp.setText(tempText);
            condition.setText(hourlyForecast.getWeatherCondition());
        }
    }

    @NonNull
    @Override
    public ForecastHourlyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forecast_item, parent, false);
        final ForecastHourlyHolder viewHolder = new ForecastHourlyHolder(view);
//        viewGroup = parent;
//        showIntro("You can scroll down to view the weather of each 3 hours of this day.",
//                parent.findViewById(R.id.forecast_hourly_recyclerview),
//                "forecast_hourly_recycler_view");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastHourlyHolder holder, int position) {
//        viewGroup.removeAllViews();
//        View view = LayoutInflater.from(context).inflate(R.layout.forecast_item, viewGroup, false);
//        viewGroup.addView(view);
        HourlyForecast hourlyForecast = hourlyForecasts.get(position);
        holder.setDetails(hourlyForecast, context);

    }

    @Override
    public int getItemCount() {
        return hourlyForecasts.size();
    }

    private void showIntro(String text, View view, String showCaseID) {
        new MaterialShowcaseView.Builder((Activity)context)
                .setTarget(view)
                .setDismissText("GOT IT")
                .setContentText(text)
                .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(showCaseID) // provide a unique ID used to ensure it is only shown once
                .show();
    }

}
